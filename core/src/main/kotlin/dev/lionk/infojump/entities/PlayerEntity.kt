package dev.lionk.infojump.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import dev.lionk.infojump.Main
import dev.lionk.infojump.actions.ActionManager
import dev.lionk.infojump.data.Settings
import dev.lionk.infojump.logic.PhysicsEngine
import dev.lionk.infojump.rendering.TextureManager
import dev.lionk.infojump.views.GameView


class PlayerEntity (
    physicsEngine: PhysicsEngine,
    initialPosition: Vector2,
    private var health: Int = 3
): Entity("game.player.ninja", physicsEngine, initialPosition = initialPosition, description = "player") {

    private val texture = TextureManager.getTexture("game.player.ninja_overlay")
    private val overlaySprite = Sprite(texture)

    init {
        createBody(physicsEngine)

//        val textureFactor = 10f / texture.height
//        sprite.setSize(texture.width * textureFactor, texture.height * textureFactor)
        overlaySprite.setSize(super.sprite.width, super.sprite.height)
        colorSprite()
    }

    fun getHealth(): Int{
        return health
    }

    fun removeHealth(){
        health--
        (Main.INSTANCE.getView() as? GameView)?.ui?.updateHealth()
        if(health <=0){
            ActionManager.handleAction("finalDeath")
        }

    }

    private fun createBody(physicsEngine: PhysicsEngine) {
        // Foot sensor
        val footShape = PolygonShape().apply {
            setAsBox(super.sprite.width / 2.05f, 0.1f, Vector2(0f, -sprite.height / 2f), 0f)
        }
        val footFixtureDef = FixtureDef().apply {
            shape = footShape
            isSensor = true
        }
        body.createFixture(footFixtureDef).apply {
            userData = "feet"
        }

        footShape.dispose()
    }

    fun colorSprite(){
        sprite.setColor(Settings.playerColor)
    }

    override fun render(spriteBatch: SpriteBatch, physicsAlpha: Float){
        overlaySprite.setPosition(
            currentX(),
            currentY()
        )
        super.render(spriteBatch, physicsAlpha)

        overlaySprite.draw(spriteBatch)

    }

    var isWalkingLeft = false

    fun setWalkDirection(left: Boolean){
        if (isWalkingLeft != left) {
            changeTexture(left)
            isWalkingLeft = left
        }
    }
    private fun changeTexture(left: Boolean){
        if(left){
            sprite.texture = TextureManager.getTexture("game.player.ninja_mirrored")
            overlaySprite.texture = TextureManager.getTexture("game.player.ninja_overlay_mirrored")
        }else{
            sprite.texture = TextureManager.getTexture("game.player.ninja")
            overlaySprite.texture = TextureManager.getTexture("game.player.ninja_overlay")
        }
        colorSprite()
    }
    override fun dispose(){
        super.dispose()
    }
}
