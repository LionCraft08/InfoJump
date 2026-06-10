package dev.lionk.infojump.game

import com.google.gson.Gson
import dev.lionk.infojump.Main
import dev.lionk.infojump.level.Level
import dev.lionk.infojump.level.LevelPreset
import dev.lionk.infojump.multiplayer.MultiplayerManager
import dev.lionk.infojump.rendering.TextureManager
import dev.lionk.infojump.tick.TickManager

object GameManager {
    var game: AbstractGame? = null
        private set
    fun loadGame(id:String) {
        if(MultiplayerManager.isInMultiplayer()){
            val preset = deserializeGame(MultiplayerManager.getAsset("game.games.$id"))
            game = MultiplayerBasedGame(
                preset.levels
            )

        }else {
            val asset = TextureManager.loadAsset("game.games.$id", "json")
            val preset = deserializeGame(asset.readString())
            game = Game(
                preset.levels
            )
        }

    }

    fun endGame(){
        TickManager.stop()
        Main.INSTANCE.changeView("menu")
    }

    fun getCurrentLevel(): Level {
        return game!!.currentLevel
    }
    private fun deserializeGame(level: String): GamePreset {
        return Gson().fromJson(level, GamePreset::class.java)

    }
}
