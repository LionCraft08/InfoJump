package dev.lionk.infojump.level

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import dev.lionk.infojump.blocks.AbstractBlock
import dev.lionk.infojump.blocks.FloorBody
import dev.lionk.infojump.entities.Entity
import dev.lionk.infojump.logic.PhysicsEngine

class Level (
    private val spawnPos: Pos,
    private val blocks: MutableList<AbstractBlock> = mutableListOf(),
    private val entities: MutableList<Entity> = mutableListOf(),
){
    val physicsEngine = PhysicsEngine()
    val floor = FloorBody(physicsEngine)

    fun addBlock(block: AbstractBlock) {
        blocks.add(block)
    }

    fun render(spriteBatch: SpriteBatch) {
        blocks.forEach { it.render(spriteBatch) }
        entities.forEach { it.render(spriteBatch) }
    }
}
