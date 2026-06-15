package dev.lionk.infojump.actions

import com.badlogic.gdx.physics.box2d.Fixture
import dev.lionk.infojump.Main
import dev.lionk.infojump.game.GameManager
import dev.lionk.infojump.level.Pos
import dev.lionk.infojump.level.toPos
import dev.lionk.infojump.logic.TeleportRequest
import dev.lionk.infojump.views.GameView

object ActionManager {
    private val actions = mutableMapOf<String, (String?, Fixture?)-> Unit>()
    private val leaveActions = mutableMapOf<String, (String?)-> Unit>()

    init {
        actions["goToMenu"] = { data, fixture ->
            Main.INSTANCE.changeView("menu")
        }
        actions["zumLetztenCheckpoint"] = {data, fixture ->
            val level = GameManager.getCurrentLevel()
            GameManager.getCurrentLevel().physicsEngine.addTPRequest(
                TeleportRequest(
                    GameManager.getCurrentLevel().player.body,
                    level.lastCheckpoint.x, level.lastCheckpoint.y
                )
            )
        }
        actions["death"] = {data, fixture ->
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
        actions["finalDeath"] = {data, fixture ->
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
        actions["checkpointSetzen"] = {data, fixture ->
            val level = GameManager.getCurrentLevel()
            val game = Main.INSTANCE.getView() as GameView
            val pos = if(fixture != null) fixture.body.position
                else level.player.body.position
            if(!level.lastCheckpoint.isNear(pos.toPos())) {
                level.lastCheckpoint = pos.toPos()
                level.addEffect("game.partikel.checkpoint", pos.toPos())
                game.ui.displaySplashNotification("Checkpoint erreicht!")
            }
        }
        actions["teleport"] = {data, fixture ->
            val level = GameManager.getCurrentLevel()
            val game = Main.INSTANCE.getView() as GameView
            try {
                val parts = data!!.split(":")
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
        actions["ziel"] = {data, fixture ->
            GameManager.game?.nextLevel()?: GameManager.endGame()
        }
    }


    fun handleAction(action: String?) {
        if(action != null) println(action)
        actions[action?.substringBefore(":")]?.invoke(action?.substringAfter(":", ""), null)
    }
    fun handleLeaveAction(action: String?) {
        if(action != null) println("leave: $action")
        leaveActions[action?.substringBefore(":")]?.invoke(action?.substringAfter(":", ""))
    }
    fun handleAction(action: Fixture?) {
        if(action == null) return
        val actionData = action.userData as? String
        println("leave: $actionData")
        actions[actionData?.substringBefore(":")]?.invoke(actionData?.substringAfter(":", ""), action)
    }
}
