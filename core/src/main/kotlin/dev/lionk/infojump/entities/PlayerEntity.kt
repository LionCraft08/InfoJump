package dev.lionk.infojump.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import dev.lionk.infojump.Main
import dev.lionk.infojump.actions.ActionManager
import dev.lionk.infojump.data.Settings
import dev.lionk.infojump.game.GameManager
import dev.lionk.infojump.logic.PhysicsEngine
import dev.lionk.infojump.rendering.TextureManager
import dev.lionk.infojump.views.GameView
import kotlinx.coroutines.GlobalScope
import kotlin.math.abs


open class PlayerEntity (
    physicsEngine: PhysicsEngine,
    initialPosition: Vector2,
    private val healthAtStart: Int = 3,
    private val color: Color? = Settings.playerColor
): Entity("game.player.${Settings.texture}.${Settings.texture}", physicsEngine, initialPosition = initialPosition, description = "player") {

    private val textures = TextureManager.getTextureSet("game.player.${Settings.texture}.${Settings.texture}")
    val overlayTextures = TextureManager.getTextureSet("game.player.ninja.ninja_overlay")

    private val texture = TextureManager.getTexture("game.player.ninja_overlay")
    private val posIndicator = TextureManager.getTexture("game.player.position_arrow")
    private val overlaySprite = Sprite(texture)
    private val posSprite = Sprite(posIndicator)

    private var health: Int = healthAtStart

    init {
        val textureFactor = actualHeight / texture.height
        sprite.setSize(texture.width * textureFactor, texture.height * textureFactor)
        posSprite.setScale(0.3f)
        overlaySprite.setSize(super.sprite.width, super.sprite.height)
        if(Settings.texture == "ninja")
            colorSprite()
    }

    fun getHealth(): Int{
        return health
    }

    fun addHealth() {
        health++
        if(health > healthAtStart){
            health = healthAtStart
        }
    }

    fun removeHealth(): Boolean{
        health--
        (Main.INSTANCE.getView() as? GameView)?.ui?.updateHealth()
        if(health <=0){
            ActionManager.handleAction("finalDeath")
            return true
        }
        return false
    }
    fun resetHealth(){
        health = healthAtStart
    }


    fun colorSprite(){
        if(color == null)
            sprite.setColor(Color.WHITE)
        else
            sprite.setColor(color)
    }

    fun updateVelocity(
        vx: Float,
        vy: Float
    ){
        body.setLinearVelocity(
            vx, vy
        )
    }

    fun setOpacity(opacity: Float){
        super.sprite.setAlpha(opacity)
    }

    fun setFrozen(frozen: Boolean){
        if(frozen){
            body.setLinearVelocity(0f, 0f)
            body.type = BodyDef.BodyType.StaticBody
        }else{
            body.type = BodyDef.BodyType.DynamicBody
        }
    }

    var walking_step = 0
    var isWalking:Boolean = false
    var lastUpdate = 0f

    fun updateWalkingStep(){
        if(isWalking) {
            walking_step++
            if (walking_step >= textures.size) {
                walking_step = 0
                isWalking = false
            }
        }else{
            walking_step = 0
        }
        super.sprite.texture = textures[walking_step]

        if(Settings.texture == "ninja")
            overlaySprite.texture = overlayTextures[walking_step]

        lastUpdate = 0f
    }

    override fun render(spriteBatch: SpriteBatch, physicsAlpha: Float){
        lastUpdate += Gdx.graphics.deltaTime

        if(body.position.y < -5){
            ActionManager.handleAction("death")
        }

        if(abs(overlaySprite.x - currentX()) > 0.05f){
            isWalking = true
        } else isWalking = false
        if(lastUpdate >= 0.1f){
            updateWalkingStep()
        }

        overlaySprite.setPosition(
            currentX(),
            currentY()
        )
        super.render(spriteBatch, physicsAlpha)
        if(body.position.y >= 74f) {
            posSprite.setPosition(body.position.x -posSprite.width/2, 60f)
            posSprite.draw(spriteBatch)
        }

        if(Settings.texture == "ninja")
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
            super.sprite.setFlip(true, false)
            overlaySprite.setFlip(true, false)
      }else{
            super.sprite.setFlip(false, false)
            overlaySprite.setFlip(false, false)
      }
        if(Settings.texture == "ninja")
            colorSprite()
    }
    override fun dispose(){
        super.dispose()
    }
}
