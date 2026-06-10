package dev.lionk.infojump.level

import com.google.gson.Gson
import dev.lionk.infojump.blocks.PassthroughBlock
import dev.lionk.infojump.blocks.StaticBlock
import dev.lionk.infojump.game.AbstractGame
import dev.lionk.infojump.game.Game
import dev.lionk.infojump.multiplayer.MultiplayerManager
import dev.lionk.infojump.rendering.TextureManager

object LevelLoader {
    private val gson = Gson()

    fun loadLevel(
        level: String,
        multiplayer: Boolean = false,
    ): Level {
        val file = TextureManager.loadAsset("game.levels.$level", "json")
        return loadLevelFromDeserializedString(file.readString(), multiplayer)
    }

    fun loadLevelFromDeserializedString(levelJson: String,multiplayer: Boolean = false,):Level{
        val levelPreset = deserializeLevel(levelJson)
        val level = Level(
            spawnPos = levelPreset.spawnPoint,
            levelPreset = levelPreset
        )
        addObjects(levelPreset, level, Pos(0f, 0f),multiplayer)

        return level
    }

    private fun addObjects(levelPreset: LevelPreset, level: Level, initPos: Pos,multiplayer: Boolean = false,) {
        levelPreset.blocks.forEach { block ->
            level.addBlock(StaticBlock(
                level.physicsEngine,
                texture = block.texture,
                pos = block.pos.toVector().add(initPos.toVector()),
                height = block.height,
                width = block.width,
                rotation = block.rotation,
                restitution = block.restitution,
                friction = block.friction,
            ))
        }
        levelPreset.nonSolidBlocks?.forEach { block ->
            level.addBlock(PassthroughBlock(
                onTouch = block.onTouch,
                physicsEngine = level.physicsEngine,
                texture = block.texture,
                pos = block.pos.toVector().add(initPos.toVector()),
                height = block.height,
                width = block.width,
                rotation = block.rotation,
            ))
        }
        levelPreset.sublevels?.forEach { sublist ->
            val sublevel = if(multiplayer) MultiplayerManager.getAsset(sublist.path)
                else TextureManager.loadAsset(sublist.path, "json").readString()
            val subpreset = deserializeLevel(sublevel)
            addObjects(subpreset, level, sublist.pos)
        }
    }
    fun deserializeLevel(level: String): LevelPreset {
        return Gson().fromJson(level, LevelPreset::class.java)
    }
}
