package dev.lionk.infojump.entities

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import dev.lionk.infojump.logic.PhysicsEngine

class ControlledPlayerEntity(
    physicsEngine: PhysicsEngine,
    initialPosition: Vector2
): PlayerEntity(
    physicsEngine = physicsEngine,
    initialPosition = initialPosition,
)
{
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
}
