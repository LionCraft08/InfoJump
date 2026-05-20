package dev.lionk.infojump.blocks

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.EdgeShape
import com.badlogic.gdx.physics.box2d.Filter
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.Shape
import dev.lionk.infojump.logic.PhysicsEngine
import dev.lionk.infojump.rendering.TextureManager
import kotlin.math.PI

abstract class AbstractBlock (
    textureID: String,
    physicsEngine: PhysicsEngine,
    fixedRotation: Boolean = true,
    initialPosition: Vector2 = Vector2(0f, 0f),
    actualWidth: Float? = null,
    val angle:Float? = null, //In degree
    actualHeight: Float = 10f,
    onTouch: String?=null,
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
        bodyDef.type = BodyDef.BodyType.StaticBody
        bodyDef.fixedRotation = fixedRotation
        bodyDef.position.set(initialPosition)
        bodyDef.angle = angle?:0f
        body = physicsEngine.getWorld().createBody(bodyDef)

        val boxShape = PolygonShape().apply {
            setAsBox(sprite.width / 2f, sprite.height / 2f)
        }
        val fixtureDef = createFixture(shape = boxShape)

        body.createFixture(fixtureDef).apply {
            if(onTouch != null) {
                userData = onTouch
            }
        }

        boxShape.dispose()
    }

    protected open fun createFixture(shape: Shape): FixtureDef {
         return FixtureDef().apply {
            this.shape = shape
            density = 1f
            restitution = 0f
            friction = 0f
        }
    }

    protected open fun getUserData():String? {
        return null
    }

    fun render(spriteBatch: SpriteBatch) {

        val r = body.angle
        sprite.rotation = r * MathUtils.radiansToDegrees
        sprite.setOrigin(sprite.width / 2f, sprite.height / 2f)
        sprite.setPosition(
            body.position.x - sprite.width / 2f,
            body.position.y - sprite.height / 2f
        )

        sprite.draw(spriteBatch)
    }
}
