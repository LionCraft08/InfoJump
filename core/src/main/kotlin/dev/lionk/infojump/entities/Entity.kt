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
    actualWidth: Float? = null,
    description: String?=null,
    actualHeight: Float = 10f
){

    private val texture = TextureManager.getTexture(textureID)
    val sprite = Sprite(texture)
    var body: Body
        private set

    init {
        if(actualWidth == null) {
            val textureFactor = actualHeight / texture.height
            sprite.setSize(texture.width * textureFactor, texture.height * textureFactor)
        }else sprite.setSize(actualWidth, actualHeight)

        //sprite.setSize(texture.width.toFloat(), texture.height.toFloat())
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.fixedRotation = fixedRotation
        bodyDef.position.set(initialPosition)
        body = physicsEngine.getWorld().createBody(bodyDef)


        val shape = PolygonShape().apply {
            setAsBox(sprite.width / 2f, sprite.height / 2f)

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

    open fun render(spriteBatch: SpriteBatch){
        sprite.setPosition(
            body.position.x - sprite.width / 2f,
            body.position.y - sprite.height / 2f
        )

        sprite.draw(spriteBatch)
    }

    fun dispose(){

    }
}
