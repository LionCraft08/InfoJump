package dev.lionk.infojump.game

import dev.lionk.infojump.level.Level
import dev.lionk.infojump.level.LevelLoader
import dev.lionk.infojump.multiplayer.MultiplayerManager

class MultiplayerBasedGame(
    levels: List<String>
): AbstractGame(
    levels
) {
    override fun loadLevel(key: String): Level {
        currentLevelIndex = levels.indexOf(key)
        return LevelLoader.loadLevelFromDeserializedString(MultiplayerManager.getAsset("game.levels.$key"), true)
    }
}
