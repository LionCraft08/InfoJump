package dev.lionk.infojump.level

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import dev.lionk.infojump.blocks.AbstractBlock
import dev.lionk.infojump.blocks.FloorBody
import dev.lionk.infojump.entities.Entity
import dev.lionk.infojump.entities.PlayerEntity
import dev.lionk.infojump.logic.PhysicsEngine
import dev.lionk.infojump.logic.Timer

class Level (
    private val spawnPos: Pos,
    private val blocks: MutableList<AbstractBlock> = mutableListOf(),
    private val entities: MutableList<Entity> = mutableListOf(),
    levelPreset: LevelPreset?=null
){
    val player : PlayerEntity
    val jumpStrength: Float = levelPreset?.player?.jumpStrength?:6000f
    val moveSpeed: Float = levelPreset?.player?.moveSpeed?:37f
    private val skin = Skin(Gdx.files.internal("ui/uiskin.json"))
    var lastCheckpoint: Pos = spawnPos
    val physicsEngine =
        if(levelPreset == null) {
            PhysicsEngine()
        }else PhysicsEngine(levelPreset.gravity)
    val menuButtons:List<String>?=levelPreset?.buttons

    init {
        player = PlayerEntity(physicsEngine = physicsEngine, initialPosition = spawnPos.toVector())
    }

    val floor = FloorBody(physicsEngine)

    fun addBlock(block: AbstractBlock) {
        blocks.add(block)
    }

    fun dispose(){
        physicsEngine.dispose()
        player.dispose()
        blocks.forEach { it.dispose() }
        entities.forEach { it.dispose() }
        skin.dispose()
    }

    fun render(spriteBatch: SpriteBatch, physicsAlpha: Float) {
        blocks.forEach { it.render(spriteBatch) }
        entities.forEach { it.render(spriteBatch, physicsAlpha) }
        player.render(spriteBatch, physicsAlpha)
    }
}
