package dev.lionk.infojump.blocks

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.Shape
import dev.lionk.infojump.logic.PhysicsEngine

class PassthroughBlock(
    onTouch: String?=null,
    physicsEngine: PhysicsEngine,
    texture: String,
    pos: Vector2,
    width: Float?=null,
    height: Float,
    rotation: Float?=null,
): AbstractBlock(
    physicsEngine = physicsEngine,
    textureID = texture,
    initialPosition = pos,
    actualWidth = width,
    actualHeight = height,
    angle = rotation ?: 0f,
    onTouch = onTouch,
) {
    init {

    }

    override fun createFixture(shape: Shape): FixtureDef {
        return FixtureDef().apply {
            this.shape = shape
            density = 1f
            isSensor = true
        }
    }
}
