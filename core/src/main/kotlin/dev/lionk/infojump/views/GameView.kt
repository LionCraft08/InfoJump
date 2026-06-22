package dev.lionk.infojump.views

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import dev.lionk.infojump.LionLog
import dev.lionk.infojump.actions.ActionManager
import dev.lionk.infojump.blocks.AbstractBlock
import dev.lionk.infojump.blocks.FloorBody
import dev.lionk.infojump.data.KeyAction
import dev.lionk.infojump.data.Settings
import dev.lionk.infojump.entities.PlayerEntity
import dev.lionk.infojump.game.GameManager
import dev.lionk.infojump.level.Level
import dev.lionk.infojump.level.LevelLoader
import dev.lionk.infojump.logic.PhysicsEngine
import dev.lionk.infojump.multiplayer.MultiplayerGameAddon
import dev.lionk.infojump.multiplayer.MultiplayerManager
import dev.lionk.infojump.rendering.TextureManager
import dev.lionk.infojump.tick.TickManager
import dev.lionk.infojump.views.components.Background
import dev.lionk.infojump.views.components.UI
import kotlin.math.abs


private const val CAMERA_MOVE_OFFSET = 25

/**
 * Die Haupt-Klasse für ein Spiel
 */
class GameView(
    hasMultiplayerGameAddon: Boolean = false,
    game: String = "singleplayer_game"
): AbstractView() {

    init {
        GameManager.loadGame(game)
    }

    val camera = OrthographicCamera()
    val viewport = FitViewport(128f, 72f, camera)
    private var debugRenderer: Box2DDebugRenderer = Box2DDebugRenderer()
    private var spriteBatch: SpriteBatch = SpriteBatch()
    //var level: Level= LevelLoader.loadLevel("example_level")
    //    private set
    val ui : UI = UI()
    val multiplayerGameAddon : MultiplayerGameAddon? = if(hasMultiplayerGameAddon){
        MultiplayerGameAddon(MultiplayerManager.getPlayers())
    } else null


    private var camMovingDest: Float? = null
    private val background = Background(
        Array<TextureRegion>().apply {
            TextureManager.getTextureSet("game.background.wolken.wolke").forEach {
                add(TextureRegion(it))
            }
        },
        viewport.worldWidth,
        viewport.worldHeight
    )


    private var isTPed = false

    override fun render() {
        ScreenUtils.clear(GameManager.getCurrentLevel().backgroundColor)

        if(deathAnimationStage >= 0){
            deathAnimationStage += Gdx.graphics.deltaTime
            if(deathAnimationStage >= 1 && !isTPed) {
                isTPed = true
                ActionManager.handleAction("deathTP")
            }
            if(deathAnimationStage >= 3){
                ActionManager.handleAction("finalDeathInstant")
                isTPed = false
                deathAnimationStage = -1f
            }
        }

        //println("FPS: " + Gdx.graphics.getFramesPerSecond() + " | DT: " + Gdx.graphics.getDeltaTime());
        viewport.apply()

        camera.update()

        background.updateAndDraw(Gdx.graphics.deltaTime, spriteBatch, camera)

        // Draw background and player
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin()
        //spriteBatch.draw(TextureManager.getTexture("game.env.background"), 0f, 0f, viewport.worldWidth, viewport.worldHeight)
        GameManager.getCurrentLevel().render(spriteBatch, physicsAlpha, deathAnimationStage)
        multiplayerGameAddon?.render(Gdx.graphics.deltaTime, spriteBatch)
        spriteBatch.end()


        if(Settings.isDebugging)
            debugRenderer.render(GameManager.getCurrentLevel().physicsEngine.getWorld(), viewport.camera.combined)

        ui.render(deathAnimationStage) //UI

        //Camera

        val distToCam = camera.position.x - GameManager.getCurrentLevel().player.body.position.x
        if(camMovingDest == null && abs(distToCam) > 40 || distToCam > 50) {
            val tmp = camMovingDest == null
            camMovingDest = GameManager.getCurrentLevel().player.body.position.x - (distToCam / abs(distToCam)) * CAMERA_MOVE_OFFSET * if(distToCam > 50) -1 else 1
            if(tmp)
                background.handleCameraMove(camera.position.x, camMovingDest!!)
        }
        if(camMovingDest != null) {
            camera.position.x = MathUtils.lerp(camera.position.x, camMovingDest!!, 0.05f);
            if(abs(camera.position.x - camMovingDest!!) < 0.5) camMovingDest = null
        }

    }

    private var deathAnimationStage = -1f
    fun triggerDeath(){
        ui.updateHealth()
        if(deathAnimationStage == -1f){
            deathAnimationStage = 0f
        }
    }

    override fun dispose() {
        spriteBatch.dispose()
        ui.dispose()
        GameManager.getCurrentLevel().dispose()
        multiplayerGameAddon?.dispose()
        debugRenderer.dispose()
        TickManager.stop()
    }

    private var lastJumpTick:Long = 0
    private var physicsAlpha: Float = 0f

    override fun handleInput() {
        val level = GameManager.getCurrentLevel()
        val velocity = level.player.body.linearVelocity
        val jumpImpulse = level.jumpStrength
        val moveSpeed = level.moveSpeed

        var horizontalDirection = 0f
        if (Gdx.input.isKeyPressed(Settings.keys[KeyAction.Left]!!)) {
            horizontalDirection -= 1f
            level.player.setWalkDirection(left=true)
        }
        if (Gdx.input.isKeyPressed(Settings.keys[KeyAction.Right]!!)) {
            horizontalDirection += 1f
            level.player.setWalkDirection(left=false)
        }

        level.player.body.setLinearVelocity(horizontalDirection * moveSpeed, velocity.y)


        // Sprung handling
        if (
            Gdx.input.isKeyPressed(Settings.keys[KeyAction.Jump]!!)
        ) {
            if(level.physicsEngine.contactListener.climbBlockContacts > 0){
                level.player.body.setLinearVelocity(
                    level.player.body.linearVelocity.x,
                    25f
                )
            }else if((level.physicsEngine.contactListener.footContacts > 0
                    ||level.physicsEngine.contactListener.timeSinceLastContact() < 100)
                && System.currentTimeMillis() - lastJumpTick > 200){
                level.player.body.applyLinearImpulse(0f, jumpImpulse, level.player.body.worldCenter.x, level.player.body.worldCenter.y, true)
                lastJumpTick = System.currentTimeMillis()
            }
        }

        physicsAlpha = level.physicsEngine.update(Gdx.graphics.deltaTime)
    }

    override fun onResize(width: Int, height: Int) {
        camMovingDest = GameManager.getCurrentLevel().player.body.position.x + CAMERA_MOVE_OFFSET
        viewport.update(width, height, true)
        ui.onResize(width.toFloat(), height.toFloat())
    }


}
