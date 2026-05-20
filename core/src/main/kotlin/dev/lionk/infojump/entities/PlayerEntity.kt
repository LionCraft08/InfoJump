package dev.lionk.infojump.entities

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import dev.lionk.infojump.logic.PhysicsEngine
import dev.lionk.infojump.rendering.TextureManager

class PlayerEntity (
    physicsEngine: PhysicsEngine,
    initialPosition: Vector2
): Entity("game.player.ninja", physicsEngine, initialPosition = initialPosition, description = "player") {

    init {
        createBody(physicsEngine)
    }

    private fun createBody(physicsEngine: PhysicsEngine) {
        // Foot sensor
        val footShape = PolygonShape().apply {
            setAsBox(super.sprite.width / 2.05f, 0.1f, Vector2(0f, -sprite.height / 2f), 0f)
        }
        val footFixtureDef = FixtureDef().apply {
            shape = footShape
            isSensor = true
        }
        body.createFixture(footFixtureDef).apply {
            userData = "feet"
        }

        footShape.dispose()
    }
    override fun render(spriteBatch: SpriteBatch){
        sprite.setPosition(
            body.position.x - sprite.width / 2f,
            body.position.y - sprite.height / 2f
        )

        sprite.draw(spriteBatch)
    }

    var isWalkingLeft = false

    fun setWalkDirection(left: Boolean){
        if (isWalkingLeft != left) {
            changeTexture(left)
            isWalkingLeft = left
        }
    }
    private fun changeTexture(left: Boolean){
        if(left){
            sprite.texture = TextureManager.getTexture("game.player.ninja_mirrored")
        }else{
            sprite.texture = TextureManager.getTexture("game.player.ninja")
        }
    }
}
