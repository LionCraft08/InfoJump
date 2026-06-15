package dev.lionk.infojump.game

import dev.lionk.infojump.Main
import dev.lionk.infojump.level.Level
import dev.lionk.infojump.views.GameView

abstract class AbstractGame (
    protected val levels: List<String>
) {
    abstract fun loadLevel(key: String): Level
    var currentLevel: Level
        private set
    var currentLevelIndex = 0
        protected set

    init {
        currentLevel = loadLevel(levels[currentLevelIndex])
    }

    fun nextLevel(){
        currentLevelIndex++
        if(levels.size <= currentLevelIndex) {
            finishGame()
        }else{
            currentLevel = loadLevel(levels[currentLevelIndex])
        }
    }

    fun finishGame(){
        val gameView = Main.INSTANCE.getView() as GameView
        gameView.ui.handleFinish()
    }
}
