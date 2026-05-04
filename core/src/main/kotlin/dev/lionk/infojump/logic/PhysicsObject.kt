package dev.lionk.infojump.logic

import com.badlogic.gdx.physics.box2d.Body
import dev.lionk.infojump.rendering.TextureManager

class PhysicsObject(
    texture: String
) {
    private val texture = TextureManager.getTexture(texture)
    private val body = Body()
}
