package dev.lionk.infojump.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import dev.lionk.infojump.blocks.AbstractBlock
import dev.lionk.infojump.blocks.FloorBody
import dev.lionk.infojump.entities.PlayerEntity
import dev.lionk.infojump.level.Level
import dev.lionk.infojump.level.LevelLoader
import dev.lionk.infojump.logic.PhysicsEngine
import dev.lionk.infojump.rendering.TextureManager

class GameView: AbstractView() {
    val camera = OrthographicCamera()
    val viewport = FitViewport(128f, 72f, camera)
    private var debugRenderer: Box2DDebugRenderer = Box2DDebugRenderer()
    private var spriteBatch: SpriteBatch = SpriteBatch()
    private var level: Level= LevelLoader.loadLevel("example_level")


    override fun render() {
        ScreenUtils.clear(Color.BLACK)

        viewport.apply()
        camera.update()
        camera.position.x = MathUtils.lerp(camera.position.x, level.player.body.position.x, 0.05f);
        // Draw background and player
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin()
        spriteBatch.draw(TextureManager.getTexture("game.env.background"), 0f, 0f, viewport.worldWidth, viewport.worldHeight)
        level.render(spriteBatch)
        spriteBatch.end()

        debugRenderer.render(level.physicsEngine.getWorld(), viewport.camera.combined)

    }

    override fun dispose() {
        spriteBatch.dispose()
    }

    private var lastJumpTick:Long = 0

    override fun handleInput() {
        val velocity = level.player.body.linearVelocity
        val jumpImpulse = level.jumpStrength
        val moveSpeed = level.moveSpeed

        var horizontalDirection = 0f
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            horizontalDirection -= 1f
            level.player.setWalkDirection(left=true)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            horizontalDirection += 1f
            level.player.setWalkDirection(left=false)
        }

        level.player.body.setLinearVelocity(horizontalDirection * moveSpeed, velocity.y)


        // Jump
        if (
            Gdx.input.isKeyPressed(Input.Keys.UP)
            && level.physicsEngine.contactListener.footContacts > 0
            && System.currentTimeMillis() - lastJumpTick > 50
        ) {
            level.player.body.applyLinearImpulse(0f, jumpImpulse, level.player.body.worldCenter.x, level.player.body.worldCenter.y, true)
            lastJumpTick = System.currentTimeMillis()
        }

        level.physicsEngine.update(Gdx.graphics.deltaTime)
    }

    override fun onResize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }


}
