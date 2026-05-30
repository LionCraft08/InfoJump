package dev.lionk.infojump.actions

import dev.lionk.infojump.Main
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
            val game = Main.INSTANCE.getView() as GameView
            game.level.physicsEngine.addTPRequest(
                TeleportRequest(
                    game.level.player.body,
                    game.level.lastCheckpoint.x, game.level.lastCheckpoint.y
                )
            )
        }
        actions["death"] = {
            val game = Main.INSTANCE.getView() as GameView
            game.level.physicsEngine.addTPRequest(
                TeleportRequest(
                    game.level.player.body,
                    game.level.lastCheckpoint.x, game.level.lastCheckpoint.y
                )
            )
            game.ui.displaySplashNotification("Du bist gestorben!")
            game.level.player.removeHealth()
        }
        actions["finalDeath"] = {
            Main.INSTANCE.changeView("menu")
        }
        actions["checkpointSetzen"] = {
            val game = Main.INSTANCE.getView() as GameView
            if(!game.level.lastCheckpoint.isNear(game.level.player.body.position.toPos())) {
                game.level.lastCheckpoint = game.level.player.body.position.toPos()
                game.ui.displaySplashNotification("Checkpoint erreicht!")
            }
        }
        actions["teleport"] = {
            val game = Main.INSTANCE.getView() as GameView
            try {
                val parts = it!!.split(":")
                val pos = Pos(parts[0].toFloat(), parts[1].toFloat())
                game.level.physicsEngine.addTPRequest(
                    TeleportRequest(
                        game.level.player.body,
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
            game.ui.timer.stop()
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
