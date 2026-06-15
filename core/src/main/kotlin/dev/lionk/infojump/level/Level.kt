package dev.lionk.infojump.level

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.ParticleEmitter
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import dev.lionk.infojump.blocks.AbstractBlock
import dev.lionk.infojump.blocks.FloorBody
import dev.lionk.infojump.entities.ControlledPlayerEntity
import dev.lionk.infojump.entities.Entity
import dev.lionk.infojump.entities.PlayerEntity
import dev.lionk.infojump.logic.PhysicsEngine
import dev.lionk.infojump.logic.Timer
import dev.lionk.infojump.rendering.TextureManager

class Level (
    val spawnPos: Pos,
    private val blocks: MutableList<AbstractBlock> = mutableListOf(),
    private val entities: MutableList<Entity> = mutableListOf(),
    val backgroundColor: Color = Color.SKY,
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
    private val particles: MutableList<ParticleEffect> = mutableListOf()

    init {
        player = ControlledPlayerEntity(physicsEngine = physicsEngine, initialPosition = spawnPos.toVector())
    }

    val floor = FloorBody(physicsEngine)

    fun addBlock(block: AbstractBlock) {
        blocks.add(block)
    }

    fun addEffect(id:String, pos: Pos) {
        val effect = ParticleEffect()
        effect.load(TextureManager.loadAsset(id, "p"),
            TextureManager.loadDirectory(id.substringBeforeLast(".")))
        effect.setPosition(pos.x, pos.y)
        effect.start()
        effect.scaleEffect(0.25f)
        particles.add(effect)

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
        particles.forEach {
            it.draw(spriteBatch, Gdx.graphics.deltaTime)
        }
    }
}
