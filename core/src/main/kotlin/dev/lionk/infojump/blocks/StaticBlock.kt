package dev.lionk.infojump.blocks

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import dev.lionk.infojump.logic.PhysicsEngine

class StaticBlock(
    physicsEngine: PhysicsEngine,
    texture: String,
    pos: Vector2,
    width: Float?=null,
    height: Float,
): AbstractBlock(
    physicsEngine = physicsEngine,
    textureID = texture,
    initialPosition = pos,
    actualWidth = width,
    actualHeight = height,
) {

}
