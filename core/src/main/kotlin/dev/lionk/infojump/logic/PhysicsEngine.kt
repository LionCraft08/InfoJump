package dev.lionk.infojump.logic

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Timer
import dev.lionk.infojump.game.GameManager


private const val TIME_STEP: Float = 1 / 60f

const val CATEGORY_ENVIRONMENT: Short = 0x0001 // 1
const val CATEGORY_PLAYER: Short = 0x0002 // 2

class PhysicsEngine(
    gravity:Float=-240f,
) {
    private val world = World(Vector2(0f, gravity), true)
    private val teleportQueues = mutableListOf<Executable>()
    private val task: Timer.Task

    val contactListener = MyContactListener()
    init {
        world.setContactListener(contactListener)
        task = Timer.schedule(object : Timer.Task(){
            override fun run() {

            }
        }, 0f, TIME_STEP)

    }

    private var accumulator = 0f
    private var steps = 0

    fun addTPRequest(request: TeleportRequest){
        teleportQueues.add(request)
    }

    fun setPlayerFrozen(playerFrozen:Boolean){
        teleportQueues.add(
            object : Executable {
                override fun execute() {
                    GameManager.getCurrentLevel().player.setFrozen(playerFrozen)
                }
            }
        )
    }

    fun update(delta: Float): Float {
//        if(steps < 5) steps++
//        else{
//            accumulator += min(delta, 0.25f) // Cap delta to avoid "Spiral of Death"
//            while (accumulator >= TIME_STEP) {
//                world.step(TIME_STEP, 6, 2)
//                accumulator -= TIME_STEP
//            }
//        }

        world.step(delta.coerceAtMost(0.25f), 6, 2)
        for (request in teleportQueues) {
            request.execute()
        }
        teleportQueues.clear()
        return accumulator/TIME_STEP
    }

    private var isDisposing: Boolean = false
    fun dispose() {
        task.cancel()
        Timer.schedule(object : Timer.Task(){
            override fun run() {
                unsafeDispose()
            }
        }, 0.1f)
        //unsafeDispose()
        if(true) return
        if(!world.isLocked) {
            unsafeDispose()
            println("Disposed PhysicsEngine 1")
        }
        else isDisposing = true
    }
    private fun unsafeDispose(){
        task.cancel()
        teleportQueues.clear()
        world.dispose()
    }

    fun getWorld(): World {
        return world
    }
}

interface Executable{
    fun execute()
}
class TeleportRequest(val body: Body?, val targetX: Float, val targetY: Float):Executable{
    override fun execute() {
        if (body != null) {
            GameManager.game?.currentLevel?.physicsEngine?.contactListener?.reset()
            body.setTransform(targetX, targetY, body.angle)
            body.setLinearVelocity(0f, 0f) // Clear momentum
            body.angularVelocity = 0f
            body.isAwake = true
        }
    }
}
