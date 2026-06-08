package dev.lionk.infojump.actions

import dev.lionk.infojump.Main
import dev.lionk.infojump.game.GameManager
import dev.lionk.infojump.level.Pos
import dev.lionk.infojump.level.toPos
import dev.lionk.infojump.logic.TeleportRequest
import dev.lionk.infojump.views.GameView

object ActionManager {
    private val actions = mutableMapOf<String, (String?)-> Unit>()
    private val leaveActions = mutableMapOf<String, (String?)-> Unit>()

    init {
        actions["goToMenu"] = {
            Main.INSTANCE.changeView("menu")
        }
        actions["zumLetztenCheckpoint"] = {
            val level = GameManager.getCurrentLevel()
            GameManager.getCurrentLevel().physicsEngine.addTPRequest(
                TeleportRequest(
                    GameManager.getCurrentLevel().player.body,
                    level.lastCheckpoint.x, level.lastCheckpoint.y
                )
            )
        }
        actions["death"] = {
            val level = GameManager.getCurrentLevel()
            if(!level.player.removeHealth()) {
                level.physicsEngine.addTPRequest(
                    TeleportRequest(
                        level.player.body,
                        level.lastCheckpoint.x, level.lastCheckpoint.y
                    )
                )
            }
            (Main.INSTANCE.getView() as GameView).ui.displaySplashNotification("Du bist gestorben!")

        }
        actions["finalDeath"] = {
            val level = GameManager.getCurrentLevel()

            GameManager.getCurrentLevel().physicsEngine.addTPRequest(
                TeleportRequest(
                    GameManager.getCurrentLevel().player.body,
                    level.spawnPos.x, level.spawnPos.y
                )
            )

            level.lastCheckpoint = level.spawnPos

            level.player.resetHealth()

            (Main.INSTANCE.getView() as GameView).ui.updateHealth()
        }
        actions["checkpointSetzen"] = {
            val level = GameManager.getCurrentLevel()
            val game = Main.INSTANCE.getView() as GameView
            if(!level.lastCheckpoint.isNear(level.player.body.position.toPos())) {
                level.lastCheckpoint = level.player.body.position.toPos()
                game.ui.displaySplashNotification("Checkpoint erreicht!")
            }
        }
        actions["teleport"] = {
            val level = GameManager.getCurrentLevel()
            val game = Main.INSTANCE.getView() as GameView
            try {
                val parts = it!!.split(":")
                val pos = Pos(parts[0].toFloat(), parts[1].toFloat())
                level.physicsEngine.addTPRequest(
                    TeleportRequest(
                        level.player.body,
                        pos.x, pos.y
                    )
                )
                game.ui.displaySplashNotification("Teleportiert!")
            }catch (e:Exception){
                game.ui.displaySplashNotification("Teleport fehlgeschlagen!")
            }
        }
        actions["ziel"] = {
            val game = Main.INSTANCE.getView() as GameView
            GameManager.game?.nextLevel()?:game.ui.timer.stop()
        }
    }


    fun handleAction(action: String?) {
        if(action != null) println(action)
        actions[action?.substringBefore(":")]?.invoke(action?.substringAfter(":", ""))
    }
    fun handleLeaveAction(action: String?) {
        if(action != null) println("leave: $action")
        leaveActions[action?.substringBefore(":")]?.invoke(action?.substringAfter(":", ""))
    }
}
