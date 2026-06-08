package dev.lionk.infojump.game

import dev.lionk.infojump.Main
import dev.lionk.infojump.level.Level
import dev.lionk.infojump.level.LevelLoader
import dev.lionk.infojump.views.components.UI

class Game(
    levels: List<String>
) : AbstractGame(
    levels
){


    override fun loadLevel(key: String):Level {
        currentLevelIndex = levels.indexOf(key)
        return LevelLoader.loadLevel(key)
    }


}
