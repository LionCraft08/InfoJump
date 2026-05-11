package dev.lionk.infojump.level

import com.badlogic.gdx.math.Vector2
import com.google.gson.Gson
import dev.lionk.infojump.blocks.AbstractBlock
import dev.lionk.infojump.blocks.StaticBlock
import dev.lionk.infojump.rendering.TextureManager

object LevelLoader {
    private val gson = Gson()

    fun loadLevel(
        level: String
    ): Level {
        val file = TextureManager.loadAsset("game.levels.$level", "json")
        val levelPreset = gson.fromJson(file.readString(), LevelPreset::class.java)
        val level = Level(
            spawnPos = levelPreset.spawnPoint
        )
        levelPreset.blocks.forEach { block -> level.addBlock(StaticBlock(
            level.physicsEngine,
            texture = block.texture,
            pos = block.pos.toVector(),
            height = block.height,
            width = block.width
        )) }

        return level
    }
}
