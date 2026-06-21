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


open class PlayerEntity (
    physicsEngine: PhysicsEngine,
    initialPosition: Vector2,
    private val healthAtStart: Int = 3,
    private val color: Color = Settings.playerColor
): Entity("game.player.ninja", physicsEngine, initialPosition = initialPosition, description = "player") {

    private val texture = TextureManager.getTexture("game.player.ninja_overlay")
    private val posIndicator = TextureManager.getTexture("game.player.position_arrow")
    private val overlaySprite = Sprite(texture)
    private val posSprite = Sprite(posIndicator)

    private var health: Int = healthAtStart

    init {
//        val textureFactor = 10f / texture.height
//        sprite.setSize(texture.width * textureFactor, texture.height * textureFactor)
        posSprite.setScale(0.3f)
        overlaySprite.setSize(super.sprite.width, super.sprite.height)
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

    override fun render(spriteBatch: SpriteBatch, physicsAlpha: Float){
        overlaySprite.setPosition(
            currentX(),
            currentY()
        )
        super.render(spriteBatch, physicsAlpha)
        if(body.position.y >= 74f) {
            posSprite.setPosition(body.position.x -posSprite.width/2, 60f)
            posSprite.draw(spriteBatch)
        }

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
