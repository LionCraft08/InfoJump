package dev.lionk.infojump.tick

import com.badlogic.gdx.math.Vector2
import dev.lionk.infojump.LionLog
import dev.lionk.infojump.game.GameManager
import dev.lionk.infojump.multiplayer.MultiplayerManager
import java.util.concurrent.atomic.AtomicBoolean

object TickManager {

    const val tickRateMs: Long = 100L
    private val running = AtomicBoolean(false)
    private var thread: Thread? = null

    /**
     * Starts the tick loop in a dedicated background thread.
     */
    fun start() {
        if (running.getAndSet(true)) return // Already running

        thread = Thread {
            println("Tick engine started.")

            while (running.get()) {
                val startTime = System.currentTimeMillis()

                try {
                    performTick()
                } catch (e: Exception) {
                    System.err.println("Error during tick: ${e.message}")
                }

                val endTime = System.currentTimeMillis()
                val elapsed = endTime - startTime
                val waitTime = tickRateMs - elapsed

                if (waitTime > 0) {
                    try {
                        Thread.sleep(waitTime)
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                    }
                } else if (elapsed > tickRateMs) {
                    // Logic for "Server Hanging" or "Can't keep up!"
                    println("Tick took too long! ($elapsed ms / $tickRateMs ms)")
                }
            }

            println("Tick engine stopped gracefully.")
        }.apply {
            name = "TickThread"
            start()
        }
    }

    /**
     * Signals the loop to stop. The thread will finish its
     * current tick before exiting.
     */
    fun stop() {
        LionLog.debug("Trying to stop Tick engine")
        running.set(false)
        thread?.interrupt()
        println("Tick engine stop command successful.")
    }
    private var lastSentPos: Vector2? = null

    private fun performTick() {
        // Your logic goes here
        val bodyPos = GameManager.getCurrentLevel().player.body.position
        if(lastSentPos == null) {
            MultiplayerManager.sendPositionUpdate(bodyPos.x, bodyPos.y)
            lastSentPos = bodyPos.cpy()
            LionLog.debug("Sending initial update")
        }else lastSentPos?.let {
            if(it.dst(bodyPos) > 0.01) {
                MultiplayerManager.sendPositionUpdate(
                    bodyPos.x, bodyPos.y,
                )
                lastSentPos = bodyPos.cpy()
            }
        }
    }

}
