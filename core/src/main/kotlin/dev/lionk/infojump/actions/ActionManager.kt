package dev.lionk.infojump.actions

import dev.lionk.infojump.Main
import dev.lionk.infojump.level.toPos
import dev.lionk.infojump.logic.TeleportRequest
import dev.lionk.infojump.views.GameView

object ActionManager {
    private val actions = mutableMapOf<String, ()-> Unit>()
    private val leaveActions = mutableMapOf<String, ()-> Unit>()

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
        actions["checkpointSetzen"] = {
            val game = Main.INSTANCE.getView() as GameView
            if(!game.level.lastCheckpoint.isNear(game.level.player.body.position.toPos())) {
                game.level.lastCheckpoint = game.level.player.body.position.toPos()
                game.ui.displaySplashNotification("Checkpoint erreicht!")
            }
        }
        actions["ziel"] = {
            val game = Main.INSTANCE.getView() as GameView
            game.ui.timer.stop()
        }
    }


    fun handleAction(action: String?) {
        if(action != null) println(action)
        actions[action]?.invoke()
    }
    fun handleLeaveAction(action: String?) {
        if(action != null) println("leave: $action")
        leaveActions[action]?.invoke()
    }
}
