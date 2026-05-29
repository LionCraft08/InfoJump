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
    var actualWidth: Float? = null,
    val angle:Float? = null, //In degree
    val fitSize: Boolean = false,
    actualHeight: Float = 10f,
    onTouch: String?=null,
){
    private val texture = BlockTexture(TextureManager.getTexture(textureID), actualWidth, actualHeight, fitSize)
    //val sprite = Sprite(texture)
    var body: Body
        private set

    init {
//        if(fitSize) {
//            if (actualWidth == null) {
//                val textureFactor = actualHeight / texture.height
//                actualWidth = texture.width * textureFactor
//                sprite.setSize(actualWidth!!, texture.height * textureFactor)
//            } else sprite.setSize(actualWidth!!, actualHeight)
//        }else{
//            val textureFactor = 10f / texture.width
//            sprite.setSize(texture.width * textureFactor, texture.height * textureFactor)
//        }
        //sprite.setSize(texture.width.toFloat(), texture.height.toFloat())
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.StaticBody
        bodyDef.fixedRotation = fixedRotation

        bodyDef.position.set(initialPosition.add(texture.blockWidth!! / 2f, texture.blockHeight / 2f))
        bodyDef.angle = angle?:0f
        body = physicsEngine.getWorld().createBody(bodyDef)

        val boxShape = PolygonShape().apply {
            setAsBox(texture.blockWidth!! / 2f, texture.blockHeight / 2f)
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

    fun dispose(){
        //texture.texture.dispose()
    }

    protected open fun getUserData():String? {
        return null
    }

    fun render(spriteBatch: SpriteBatch) {
        texture.draw(spriteBatch, body.position.x, body.position.y, body.angle)
    }
}
