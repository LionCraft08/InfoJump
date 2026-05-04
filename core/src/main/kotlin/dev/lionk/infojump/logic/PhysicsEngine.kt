package dev.lionk.infojump.logic

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World

class PhysicsEngine {
    private val world = World(Vector2(0f, -220f), true)

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
