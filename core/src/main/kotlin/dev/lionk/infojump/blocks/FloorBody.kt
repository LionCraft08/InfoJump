package dev.lionk.infojump.blocks

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.EdgeShape
import dev.lionk.infojump.logic.PhysicsEngine

class FloorBody(
    physicsEngine: PhysicsEngine,
) {
    val groundBody: Body

    init {
        val groundBodyDef = BodyDef().apply {
            type = BodyDef.BodyType.StaticBody
            position.set(500f, 0f)
        }
        groundBody = physicsEngine.getWorld().createBody(groundBodyDef)
        val groundShape = EdgeShape().apply {
            set(-1000f, 0f, 1000f, 0f)

            //set(0f, 0f, 1000f, 0f)
        }
        groundBody.createFixture(groundShape, 0f)
        groundShape.dispose()
    }
}
