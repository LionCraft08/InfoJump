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
    val gravity: Float,
    val player: PlayerConfig,
    val sublevels:List<Sublevel>?=null,
    val blocks: List<BlockPreset>,
    val nonSolidBlocks: List<NonSolidBlockPreset>?=null,
)

data class Sublevel(
    val path: String,
    val pos: Pos
)

data class PlayerConfig(
    val texture: String,
    val jumpStrength: Float,
    val moveSpeed : Float,
){

}

data class BlockPreset(
    val texture: String,
    val pos: Pos,
    val width:Float?=null,
    val height:Float,
    val rotation:Float?=null,
    val restitution:Float?=null,
    val friction:Float?=null,
)
data class NonSolidBlockPreset(
    val onTouch: String?=null,
    val texture: String,
    val pos: Pos,
    val width: Float?=null,
    val height: Float,
    val rotation: Float? = null,
)
