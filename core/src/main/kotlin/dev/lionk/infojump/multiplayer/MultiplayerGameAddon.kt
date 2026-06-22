package dev.lionk.infojump.multiplayer

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import dev.lionk.infojump.entities.MultiPlayerEntity
import dev.lionk.infojump.game.GameManager
import dev.lionk.infojump.level.Pos
import dev.lionk.infojump.payloads.Player

class MultiplayerGameAddon(
    playerConfigs:List<Player>
) {
    val players: MutableMap<String,MultiPlayerEntity> = mutableMapOf()
    init {
        val level = GameManager.game!!.currentLevel
        playerConfigs.forEach { config ->
            if(config.name != MultiplayerManager.name)
                players[config.name] = MultiPlayerEntity(
                    "game.player.${config.character}.${config.character}",
                    level.spawnPos.toVector(),
                    name = config.name,
                    color = Color(config.color.toInt())
                )
        }
    }

    fun render(delta:Float, spriteBatch: SpriteBatch){
        players.forEach {
            it.value.render(spriteBatch, delta)
        }
    }

    fun updatePlayerPos(player: String, level:Int?, x: Float, y: Float) {
        players[player]?.updatePos(
            Pos(x,y),
            level
        )
    }

    fun dispose(){
        players.forEach {
            it.value.dispose()
        }
    }
}
