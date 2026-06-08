package dev.lionk.infojump.game

import dev.lionk.infojump.Main
import dev.lionk.infojump.level.Level

abstract class AbstractGame (
    protected val levels: List<String>
) {
    protected abstract fun loadLevel(key: String): Level
    var currentLevel: Level
        private set
    protected var currentLevelIndex = 0

    init {
        currentLevel = loadLevel(levels[currentLevelIndex])
    }

    fun nextLevel(){
        currentLevelIndex++
        if(levels.size <= currentLevelIndex) {
            Main.INSTANCE.changeView("menu")
        }else{
            currentLevel = loadLevel(levels[currentLevelIndex])
        }
    }
}
