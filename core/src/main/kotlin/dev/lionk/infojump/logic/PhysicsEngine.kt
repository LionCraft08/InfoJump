package dev.lionk.infojump.logic

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World

class PhysicsEngine(
    gravity:Float=-240f,
) {
    private val world = World(Vector2(0f, gravity), true)
    val contactListener = MyContactListener()
    init {
        world.setContactListener(contactListener)
    }

    fun update(delta: Float) {
        world.step(delta, 6, 2)
    }

    fun dispose() {
        world.dispose()
    }

    fun getWorld(): World {
        return world
    }
}
