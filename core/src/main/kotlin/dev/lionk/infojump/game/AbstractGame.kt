package dev.lionk.infojump.game

import dev.lionk.infojump.Main
import dev.lionk.infojump.level.Level
import dev.lionk.infojump.multiplayer.MultiplayerManager
import dev.lionk.infojump.views.GameView

abstract class AbstractGame (
    protected val levels: List<String>
) {
    abstract fun loadLevel(key: String): Level
    var currentLevel: Level
        private set
    var currentLevelIndex = 0
        protected set
    var isFinished:Boolean = false

    init {
        currentLevel = loadLevel(levels[currentLevelIndex])
    }

    fun nextLevel(){
        if(isFinished){ return }
        currentLevelIndex++
        if(levels.size <= currentLevelIndex) {
            currentLevelIndex--
            isFinished = true
            finishGame()
        }else{
            currentLevel = loadLevel(levels[currentLevelIndex])
        }

        (Main.INSTANCE.getView() as? GameView)?.ui?.updateHealth()
    }

    fun finishGame(){
        val gameView = Main.INSTANCE.getView() as GameView
        if(MultiplayerManager.isInMultiplayer())
            MultiplayerManager.sendGameFinish()
        else
            gameView.ui.handleFinish()
    }
}
