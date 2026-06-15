package dev.lionk.infojump.blocks

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.Shape
import com.badlogic.gdx.physics.box2d.joints.MouseJoint
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef
import dev.lionk.infojump.LionLog
import dev.lionk.infojump.game.GameManager
import dev.lionk.infojump.level.MovingBlock
import dev.lionk.infojump.logic.PhysicsEngine

class StaticBlock(
    private val physicsEngine: PhysicsEngine,
    texture: String,
    pos: Vector2,
    width: Float?=null,
    height: Float,
    val movement: MovingBlock? = null,
    rotation: Float?=null,
    private val friction: Float?=null,
    private val restitution: Float?=null,
): AbstractBlock(
    physicsEngine = physicsEngine,
    textureID = texture,
    initialPosition = pos,
    actualWidth = width,
    actualHeight = height,
    angle = rotation,
    bodyType = if(movement == null) BodyDef.BodyType.StaticBody else BodyDef.BodyType.KinematicBody,
) {
    private var movementStage: Int? = null
    private var delayCounter: Float? = null

    init {
        if(movement != null) {
            movementStage = 0
            delayCounter = 0f
        }
    }

    override fun createFixture(shape: Shape): FixtureDef {
        return super.createFixture(shape).apply {
            isSensor = false
            friction = this@StaticBlock.friction?:0f
            restitution = this@StaticBlock.restitution?:0f
        }
    }

    fun moveTo(body: Body, targetPos: Vector2) {
        val currentPosition = body.position

        val direction = Vector2(targetPos).sub(currentPosition)

        if (direction.len2() > 0.001f) {
            direction.nor()
            direction.scl(movement!!.speed)

            body.setLinearVelocity(direction.x, direction.y)
        }
    }

    fun checkAndStopAtTarget(body: Body, target: Vector2, tolerance: Float = 0.5f): Boolean {
        val currentPosition = body.position

        val distanceSquared = currentPosition.dst2(target)
        val toleranceSquared = tolerance * tolerance

        if (distanceSquared <= toleranceSquared) {
            body.setLinearVelocity(0f, 0f)

            body.setTransform(target, body.angle)
            return true
        }

        return false
    }

    private fun getPositionToMoveTo():Vector2 {
        return getActualPosition(Vector2(if(movementStage == 1) movement!!.targetPos.toVector() else initialPosition))
    }

    override fun render(spriteBatch: SpriteBatch) {
        if(movementStage != null) {
            checkAndStopAtTarget(
                super.body,
                getPositionToMoveTo()
            )

        }

        if (movementStage != null) {
            if (movementStage!! % 2 == 0) {
                delayCounter = delayCounter?.plus(Gdx.graphics.deltaTime)
                if(delayCounter!!>=movement!!.waitDuration){
                    startNextStage()
                }
            }else if(
                checkAndStopAtTarget(
                    super.body,
                    getPositionToMoveTo()
                )
            ){
                startNextStage()
            }
        }

//        if(mouseJoint != null) {
//            when(movementStage!!){
//                1 -> moveTo(super.body, movement!!.targetPos.toVector())
//                3 -> moveTo(super.body, initialPosition)
//            }
//        }

        super.render(spriteBatch)

    }

    fun startNextStage(){
        movementStage = movementStage!!.plus(1)%4
        delayCounter = 0f
        when(movementStage!!){
            1 -> moveTo(super.body, getPositionToMoveTo())
            3 -> moveTo(super.body, getPositionToMoveTo())
        }
    }

}
