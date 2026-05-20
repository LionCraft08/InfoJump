package dev.lionk.infojump.level

import com.google.gson.Gson
import dev.lionk.infojump.blocks.PassthroughBlock
import dev.lionk.infojump.blocks.StaticBlock
import dev.lionk.infojump.rendering.TextureManager

object LevelLoader {
    private val gson = Gson()

    fun loadLevel(
        level: String
    ): Level {
        val file = TextureManager.loadAsset("game.levels.$level", "json")
        val levelPreset = deserializeLevel(file.readString())
        val level = Level(
            spawnPos = levelPreset.spawnPoint,
            levelPreset = levelPreset
        )
        addObjects(levelPreset, level, Pos(0f, 0f))

        return level
    }
    private fun addObjects(levelPreset: LevelPreset, level: Level, initPos: Pos) {
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
            val sublevel = TextureManager.loadAsset(sublist.path, "json")
            val subpreset = deserializeLevel(sublevel.readString())
            addObjects(subpreset, level, sublist.pos)
        }
    }
    fun deserializeLevel(level: String): LevelPreset {
        return Gson().fromJson(level, LevelPreset::class.java)

    }
}
