package dev.lionk.infojump.level

import com.badlogic.gdx.math.Vector2
import kotlin.math.abs

data class Pos(
    val x: Float,
    val y: Float,
){
    fun toVector() = Vector2(x, y)
    fun isNear(pos: Pos): Boolean{
        return abs(this.x - pos.x) < 3 && abs(this.y - pos.y) < 3
    }
}

fun Vector2.toPos(): Pos{
    return Pos(x,y)
}

data class LevelPreset(
    val name: String,
    val id: Int,
    val boden:Boolean?=false,
    val spawnPoint: Pos,
    val color: String?=null,
    val buttons:List<String>?=null,
    val gravity: Float,
    val player: PlayerConfig,
    val sublevels:List<Sublevel>?=null,
    val blocks: List<BlockPreset>,
    val nonSolidBlocks: List<NonSolidBlockPreset>?=null,
)

data class MovingBlock(
    val speed:Float,
    val targetPos:Pos,
    val waitDuration:Float,
){

}

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
    val fitSize: Boolean?=false,
    val restitution:Float?=null,
    val movement: MovingBlock?=null,
    val friction:Float?=null,
)
data class NonSolidBlockPreset(
    val onTouch: String?=null,
    val texture: String,
    val pos: Pos,
    val width: Float?=null,
    val height: Float,
    val climbable:Boolean?=false,
    val rotation: Float? = null,
)
