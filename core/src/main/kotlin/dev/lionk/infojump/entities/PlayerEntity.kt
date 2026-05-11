package dev.lionk.infojump.entities

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import dev.lionk.infojump.logic.PhysicsEngine
import dev.lionk.infojump.rendering.TextureManager

class PlayerEntity (
    physicsEngine: PhysicsEngine
): Entity("game.player.ninja", physicsEngine){

    init {
        createBody(physicsEngine)
    }

    private fun createBody(physicsEngine: PhysicsEngine) {
        // Foot sensor
        val footShape = PolygonShape().apply {
            setAsBox(super.sprite.width / 2.2f, 0.1f, Vector2(0f, -sprite.height / 2f), 0f)
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
}
