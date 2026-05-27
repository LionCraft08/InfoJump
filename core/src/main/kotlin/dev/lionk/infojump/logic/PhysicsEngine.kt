package dev.lionk.infojump.logic

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import kotlin.math.min


private const val TIME_STEP: Float = 1 / 60f

class PhysicsEngine(
    gravity:Float=-240f,
) {
    private val world = World(Vector2(0f, gravity), true)
    private val teleportQueues = mutableListOf<TeleportRequest>()

    val contactListener = MyContactListener()
    init {
        world.setContactListener(contactListener)
    }

    private var accumulator = 0f

    fun addTPRequest(request: TeleportRequest){
        teleportQueues.add(request)
    }

    fun update(delta: Float) {
        accumulator += min(delta, 0.25f) // Cap delta to avoid "Spiral of Death"

        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, 6, 2)
            accumulator -= TIME_STEP
        }
        for (request in teleportQueues) {
            if (request.body != null) {
                request.body.setTransform(request.targetX, request.targetY, request.body.angle)
                request.body.setLinearVelocity(0f, 0f) // Clear momentum
                request.body.angularVelocity = 0f
                request.body.isAwake = true
            }
        }
        teleportQueues.clear()
    }

    fun dispose() {
        world.dispose()
    }

    fun getWorld(): World {
        return world
    }
}

class TeleportRequest(val body: Body?, val targetX: Float, val targetY: Float)
