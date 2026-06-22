package dev.lionk.infojump.entities

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import dev.lionk.infojump.logic.PhysicsEngine
import dev.lionk.infojump.rendering.TextureManager

abstract class Entity (
    textureID: String,
    physicsEngine: PhysicsEngine,
    fixedRotation: Boolean = true,
    initialPosition: Vector2 = Vector2(0f, 20f),
    val actualWidth: Float = 8f,
    description: String?=null,
    val actualHeight: Float = 10f
){

    private val texture = TextureManager.getTexture(textureID)
    val sprite = Sprite(texture)
    var body: Body
        private set

    init {
        val textureFactor = actualHeight / texture.height
        sprite.setSize(texture.width * textureFactor, texture.height * textureFactor)

//        if(actualWidth == null) {
//            val textureFactor = actualHeight / texture.height
//            sprite.setSize(texture.width * textureFactor, texture.height * textureFactor)
//        }else sprite.setSize(actualWidth, actualHeight)

        //sprite.setSize(texture.width.toFloat(), texture.height.toFloat())
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.fixedRotation = fixedRotation
        bodyDef.position.set(initialPosition)
        body = physicsEngine.getWorld().createBody(bodyDef)


        val shape = PolygonShape().apply {
            setAsBox(actualWidth!! / 2f, actualHeight / 2f)

        }
        val fixtureDef = FixtureDef().apply {
            this.shape = shape
            density = 1f
            friction = 0f
            restitution = 0f
        }

        body.createFixture(fixtureDef).apply {
            if(description != null) {
                userData = description
            }
        }

        shape.dispose()
    }

    fun teleport(x: Float, y: Float){
        body.setTransform(x, y, body.angle)
    }

    protected var previousX: Float = initialPosition.x
    protected var previousY: Float = initialPosition.y

    open fun render(spriteBatch: SpriteBatch, physicsAlpha: Float){
        sprite.setPosition(
            currentX(),
            currentY()
        )

        sprite.draw(spriteBatch)

        previousX = currentX()
        previousY = currentY()
    }

    protected fun currentX(): Float = body.position.x - sprite.width / 2f
    protected fun currentY(): Float = body.position.y - sprite.height / 2f

    open fun dispose(){
        //texture.dispose()
    }
}
