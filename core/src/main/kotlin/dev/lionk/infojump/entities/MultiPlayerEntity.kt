package dev.lionk.infojump.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Align
import dev.lionk.infojump.data.Settings
import dev.lionk.infojump.game.GameManager
import dev.lionk.infojump.level.Pos
import dev.lionk.infojump.level.toPos
import dev.lionk.infojump.logic.PhysicsEngine
import dev.lionk.infojump.rendering.TextureManager
import dev.lionk.infojump.tick.TickManager
import dev.lionk.infojump.views.createFont
import javax.swing.Spring.width
import kotlin.math.abs


class MultiPlayerEntity(
    val textureID: String,
    initialPosition: Vector2 = Vector2(0f, 20f),
    actualWidth: Float = 8f,
    actualHeight: Float = 10f,
    val name: String,
    val color: Color? = Settings.playerColor
) {
    private val font = createFont(32, color?:Color.WHITE, borderSize = 1f)

    private val textures = TextureManager.getTextureSet(textureID)
    val overlayTextures = TextureManager.getTextureSet("game.player.ninja.ninja_overlay")
    private val texture = TextureManager.getTexture(textureID)
    val sprite = Sprite(texture)
    private val overlayTexture = TextureManager.getTexture("game.player.ninja.ninja_overlay")
    private val overlaySprite = Sprite(overlayTexture)
    private var shouldBeRendered = true
    init {
        font.setUseIntegerPositions(false);
        //font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        val textureFactor = actualHeight / texture.height
        sprite.setSize(texture.width * textureFactor, texture.height * textureFactor)
        if(textureID.endsWith("ninja"))
            colorSprite()
        overlaySprite.setSize(sprite.width, sprite.height)
    }

    protected var previousPos: Pos = initialPosition.toPos()
    protected var currentPos: Pos = initialPosition.toPos()
    private var lastPosUpdate: Long = 0

    fun updatePos(currentPos: Pos, level: Int?) {
        if(level != GameManager.game?.currentLevelIndex) {
            shouldBeRendered = false
        }else{
            shouldBeRendered = true
        }
        lastPosUpdate = System.currentTimeMillis()
        this.previousPos = this.currentPos
        this.currentPos = currentPos
        if(previousPos.x - currentPos.x < 0) {
            setWalkDirection(false)
        }else if(previousPos.x - currentPos.x > 0) {
            setWalkDirection(left=true)
        }
    }

    fun colorSprite(){
        if(color == null)
            sprite.setColor(Color.WHITE)
        else
            sprite.setColor(color)
    }

    fun dispose() {

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
        sprite.texture = textures[walking_step]

        if(textureID.endsWith("ninja"))
            overlaySprite.texture = overlayTextures[walking_step]

        lastUpdate = 0f
    }

    private var previousRenderedX:Float = 0f


    fun render(spriteBatch: SpriteBatch, physicsAlpha: Float){
        if(!shouldBeRendered) return

        val tickAlpha: Float = ((System.currentTimeMillis() - lastPosUpdate) / TickManager.tickRateMs.toFloat()).coerceAtMost(1f)
        val renderX = MathUtils.lerp(previousPos.x, currentPos.x, tickAlpha) - (sprite.width / 2f)
        val renderY = MathUtils.lerp(previousPos.y, currentPos.y, tickAlpha) - (sprite.height / 2f)

        lastUpdate += Gdx.graphics.deltaTime
        if(abs(previousRenderedX - renderX) > 0.1f){
            isWalking = true
        } else isWalking = false
        if(lastUpdate >= 0.1f){
            updateWalkingStep()
        }

        sprite.setPosition(renderX, renderY)
        overlaySprite.setPosition(renderX, renderY)
        sprite.draw(spriteBatch)
        if(textureID.endsWith("ninja"))
            overlaySprite.draw(spriteBatch)
        font.data.setScale(0.1f)
        font.draw(spriteBatch, name, renderX, renderY+(sprite.height + 2))

        previousRenderedX = renderX
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
            sprite.setFlip(true, false)
            overlaySprite.setFlip(true, false)
        }else{
            sprite.setFlip(false, false)
            overlaySprite.setFlip(false, false)
        }
        if(textureID.endsWith("ninja"))
            colorSprite()
    }
}
