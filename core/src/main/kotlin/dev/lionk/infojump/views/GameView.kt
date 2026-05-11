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
    var player : PlayerEntity
    private var spriteBatch: SpriteBatch = SpriteBatch()
    private var level: Level= LevelLoader.loadLevel("example_level")

    init {
        player=PlayerEntity(level.physicsEngine)
    }

    override fun render() {
        ScreenUtils.clear(Color.BLACK)

        viewport.apply()
        camera.update()
        camera.position.x = MathUtils.lerp(camera.position.x, player.body.position.x, 0.05f);
        // Draw background and player
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin()
        spriteBatch.draw(TextureManager.getTexture("game.env.background"), 0f, 0f, viewport.worldWidth, viewport.worldHeight)
        level.render(spriteBatch)
        player.render(spriteBatch)
        spriteBatch.end()

        debugRenderer.render(level.physicsEngine.getWorld(), viewport.camera.combined)

    }

    override fun dispose() {
        spriteBatch.dispose()
    }

    override fun handleInput() {
        val velocity = player.body.linearVelocity
        val jumpImpulse = 6000f
        val moveSpeed = 38f

        var horizontalDirection = 0f
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            horizontalDirection -= 1f
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            horizontalDirection += 1f
        }

        player.body.setLinearVelocity(horizontalDirection * moveSpeed, velocity.y)


        // Jump
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && level.physicsEngine.contactListener.footContacts > 0) {
            player.body.applyLinearImpulse(0f, jumpImpulse, player.body.worldCenter.x, player.body.worldCenter.y, true)
        }

        level.physicsEngine.update(Gdx.graphics.deltaTime)
    }

    override fun onResize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }


}
