package dev.lionk.infojump.actions

import dev.lionk.infojump.Main
import dev.lionk.infojump.entities.PlayerEntity

object ActionManager {
    private val actions = mutableMapOf<String, ()-> Unit>()
    private val leaveActions = mutableMapOf<String, ()-> Unit>()

    init {
        actions["goToMenu"] = {
            Main.INSTANCE.changeView("menu")
        }
    }


    fun handleAction(action: String?) {
        if(action != null) println(action)
        actions[action]?.invoke()
    }
    fun handleLeaveAction(action: String?) {
        if(action != null) println(action)
        leaveActions[action]?.invoke()
    }
}
