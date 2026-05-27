package dev.lionk.infojump.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.physics.box2d.Box2D
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import dev.lionk.infojump.rendering.TextureManager
import dev.lionk.infojump.views.menu.MenuBackground


class MenuView(
    val onStartGame: ()->Unit = {}
): AbstractView(

) {
    var skin: Skin? = null
    var stage: Stage? = null
    val font: BitmapFont

    val background: MenuBackground

    init {

        stage = Stage(ScreenViewport())

        Gdx.input.setInputProcessor(stage)

        skin = Skin(Gdx.files.internal("ui/uiskin.json"))

        //Font generator
        val generator = FreeTypeFontGenerator(TextureManager.loadAsset("ui/pixelfont", "ttf"))
        val parameter: FreeTypeFontGenerator.FreeTypeFontParameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 32
        parameter.color = Color.WHITE

        parameter.borderWidth = 1f
        parameter.borderColor = Color.BLACK

        font = generator.generateFont(parameter)

        generator.dispose()
        //end

        val table = Table()
        table.setFillParent(false) // Make the table the size of the screen
        //table.setDebug(true)
        table.isTransform = true
        table.rotation = 15f
        table.x = 300f
        table.y = 300f
        table.scaleBy(2f)
        //table.scaleBy(stage!!.width/640, stage!!.height/480)
        //table.setOrigin(table.width, table.height)

        background = MenuBackground(stage!!)

        stage!!.addActor(table)

        val textStyle = TextButton.TextButtonStyle(null, null, null, font)
        val playButton = TextButton("Singleplayer", textStyle)
        val settingsButton = TextButton("Einstellungen",textStyle)


        playButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                onStartGame()
            }
        })
        playButton.setScale(2f)


        settingsButton.setX(100f, 1)
        // Add the button to the table
        table.add(playButton).fillX().uniformX()
        table.row()
        table.add(settingsButton).width(100f).uniformX()
    }

    private var alpha = 0f

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        background.render(Gdx.graphics.deltaTime)
        stage!!.act(Gdx.graphics.deltaTime/*.coerceAtMost(1 / 30f)*/);

        stage!!.draw();

        alpha += 0.01f
    }

    override fun dispose() {
        stage!!.dispose()
        skin!!.dispose()
    }

    override fun handleInput() {

    }

    override fun onResize(width: Int, height: Int) {
        background.onResize(width, height)
        //topImage.height = height.toFloat()
        stage?.viewport?.update(width, height, true)

    }
//    private fun resizeBackgroundImage(newWidth:Float? = null, newHeight:Float? = null) {
//        val image = topImage
//        if(newWidth != null) {
//            val rescaleFactor = newWidth / image.width
//            image.width = newWidth
//            image.height *= rescaleFactor
//        } else
//        if(newHeight != null) {
//            val rescaleFactor = newHeight / image.height
//            image.height = newHeight
//            image.width *= rescaleFactor
//        }
//    }
}
