package dev.lionk.infojump

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport


/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms. */
class MainOld : ApplicationAdapter() {
    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var playerTexture: Texture

    private lateinit var playerSprite: Sprite

    private lateinit var backgroundTexture: Texture
    private lateinit var spriteBatch: SpriteBatch
    private lateinit var viewport: FitViewport

    // Box2D variables
    private lateinit var world: World
    private lateinit var playerBody: Body
    private lateinit var debugRenderer: Box2DDebugRenderer

    // Box platform variables
    private lateinit var boxBody: Body
    private val boxWidth = 20f
    private val boxHeight = 5f

    override fun create() {
        shapeRenderer = ShapeRenderer()
        //playerTexture = Texture("hund.png")
        //spriteBatch = SpriteBatch()
        viewport = FitViewport(128f, 72f)
        viewport.camera.position.x
        backgroundTexture = Texture("background.jpg")
//        playerSprite = Sprite(playerTexture)
//        val playerWidthFactor = playerTexture.width/12f
//        val playerHeight = playerTexture.height/playerWidthFactor
//        val playerWidth = playerTexture.width/playerWidthFactor
//        playerSprite.setSize(playerWidth, playerHeight)

       // Box2D.init()
        world = World(Vector2(0f, -2f), true)
        debugRenderer = Box2DDebugRenderer()

        // 2. Create Ground Body (Static)
//        val groundBodyDef = BodyDef().apply {
//            type = BodyDef.BodyType.StaticBody
//            position.set(viewport.worldWidth / 2f, 0f)
//        }
//        val groundBody = world.createBody(groundBodyDef)
//        val groundShape = EdgeShape().apply {
//            set(-viewport.worldWidth / 2f, 0f, viewport.worldWidth / 2f, 0f)
//        }
//        groundBody.createFixture(groundShape, 0f)
//        groundShape.dispose()

        // 3. Create a Box Platform (Static)
        val boxBodyDef = BodyDef().apply {
            type = BodyDef.BodyType.StaticBody
            position.set(viewport.worldWidth / 2f, 10f) // Positioned above ground

        }
        boxBody = world.createBody(boxBodyDef)
        val boxShape = PolygonShape().apply {
            setAsBox(boxWidth / 2f, boxHeight / 2f)
        }
        boxBody.createFixture(boxShape, 0f)
        boxShape.dispose()

        // 4. Create Player Body (Dynamic)
//        val playerBodyDef = BodyDef().apply {
//            type = BodyDef.BodyType.DynamicBody
//            position.set(viewport.worldWidth / 2f, 40f)
//            fixedRotation = true
//        }
//
//        playerBody = world.createBody(playerBodyDef)
//
//        val playerShape = PolygonShape().apply {
//            setAsBox(playerSprite.width / 2f, playerSprite.height / 2f)
//        }
//        val fixtureDef = FixtureDef().apply {
//            shape = playerShape
//            density = 1f
//            friction = 0f
//            restitution = 0f
//        }
//        playerBody.createFixture(fixtureDef)
//        playerShape.dispose()
    }

    override fun render() {
        input()
        logic()
        draw()
    }

    private fun input() {

//
//        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT) ) {
//            applyXMovement(moveSpeed, Input.Keys.LEFT)
//        }else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
//            applyXMovement(moveSpeed, Input.Keys.RIGHT)
//        }else{
//            applyXMovement(0f, null)
//        }
    }

    private fun logic() {
        world.step(1/60f, 6, 2)

        playerSprite.setPosition(
            playerBody.position.x - playerSprite.width / 2f,
            playerBody.position.y - playerSprite.height / 2f
        )
    }

    private fun draw() {
        //ScreenUtils.clear(Color.BLACK)
        //viewport.apply(false)

//        // Draw background and player
//        spriteBatch.projectionMatrix = viewport.camera.combined
//        spriteBatch.begin()
//        spriteBatch.draw(backgroundTexture, 0f, 0f, 128f, 72f)
//        playerSprite.draw(spriteBatch)
//        spriteBatch.end()

        // Draw the box platform using ShapeRenderer
        shapeRenderer.projectionMatrix = viewport.camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.NAVY
        shapeRenderer.rect(
            boxBody.position.x - boxWidth / 2f,
            boxBody.position.y - boxHeight / 2f,
            boxWidth,
            boxHeight
        )
        shapeRenderer.end()

        // Debug renderer helps visualize Box2D bodies (optional)
        //debugRenderer.render(world, viewport.camera.combined)
    }


    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        shapeRenderer.dispose()
        world.dispose()
        debugRenderer.dispose()
        playerTexture.dispose()
        backgroundTexture.dispose()
        spriteBatch.dispose()
    }
}
