package dev.lionk.infojump.multiplayer

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import dev.lionk.infojump.entities.PlayerEntity
import dev.lionk.infojump.game.GameManager
import dev.lionk.infojump.logic.PhysicsEngine
import dev.lionk.infojump.payloads.Player

class MultiplayerGameAddon(
    playerConfigs:List<Player>
) {
    val players: MutableList<PlayerEntity> = mutableListOf()
    init {
        val level = GameManager.game!!.currentLevel
        playerConfigs.forEach {
            players.add(PlayerEntity(
                level.physicsEngine,
                level.spawnPos.toVector(),
                color = Color(it.color.toInt())
            ))
        }
    }

    fun render(delta:Float, spriteBatch: SpriteBatch){
        players.forEach {
            it.render(spriteBatch, delta)
        }
    }
    fun dispose(){
        players.forEach {
            it.dispose()
        }
    }
}
