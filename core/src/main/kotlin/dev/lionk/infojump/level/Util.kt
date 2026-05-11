package dev.lionk.infojump.level

import com.badlogic.gdx.math.Vector2

data class Pos(
    val x: Float,
    val y: Float,
){
    fun toVector() = Vector2(x, y)
}

data class LevelPreset(
    val name: String,
    val id: Int,
    val spawnPoint: Pos,
    val blocks: List<BlockPreset>,
)

data class BlockPreset(
    val texture: String,
    val pos: Pos,
    val width:Float?,
    val height:Float,
)
