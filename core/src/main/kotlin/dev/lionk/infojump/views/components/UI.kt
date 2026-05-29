package dev.lionk.infojump.views.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import dev.lionk.infojump.actions.ActionManager
import dev.lionk.infojump.data.Settings
import dev.lionk.infojump.level.Level
import dev.lionk.infojump.logic.Timer
import dev.lionk.infojump.rendering.TextureManager


private const val SPLASH_SCREEN_TIME = 2500L

class UI (
    level: Level?=null
){
    private val font: BitmapFont
    private var uiBatch: SpriteBatch = SpriteBatch()
    private val uiCamera: OrthographicCamera = OrthographicCamera() // Added for UI camera
    private val uiViewport: ScreenViewport = ScreenViewport(uiCamera)

    val timer = Timer()
    private var splashNotification: String? = null
    private var splashNotificationSetTime: Long=0
    private val stage: Stage
    private val skin = Skin(Gdx.files.internal("ui/uiskin.json"))

    init {
        timer.start()
        val generator = FreeTypeFontGenerator(TextureManager.loadAsset("ui/pixelfont", "ttf"))


        val parameter: FreeTypeFontGenerator.FreeTypeFontParameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 64 // The actual target size in pixels
        parameter.color = Color.WHITE

        parameter.borderWidth = 1f
        parameter.borderColor = Color.BLACK

        font = generator.generateFont(parameter)

        stage = Stage(uiViewport)
        Gdx.input.inputProcessor = stage
        val table = Table()
        table.setFillParent(true)
        table.debug = Settings.isDebugging
        table.left()

        table.pad(20f)
        stage.addActor(table)
        addButton("zum Menü", "goToMenu", table)
        level?.menuButtons?.forEach { addButton(it, it, table) }



        generator.dispose()

    }

    private fun addButton(text: String, action: String, table: Table){
        val menuButton = TextButton(text, skin).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    ActionManager.handleAction(action)
                }
            })
        }
        table.add(menuButton)
        table.row()
    }

    fun displaySplashNotification(notification: String){
        splashNotification = notification
        splashNotificationSetTime = System.currentTimeMillis()
    }


    fun render(){
        uiViewport.apply()
        uiBatch.projectionMatrix = uiCamera.combined


        uiBatch.begin()
        stage.draw()
        timer.draw(font, uiBatch)
        if(System.currentTimeMillis()-splashNotificationSetTime < SPLASH_SCREEN_TIME){
            font.draw(uiBatch, splashNotification, 10f, 70f)
        }


        uiBatch.end()
    }

    fun dispose() {
        uiBatch.dispose()
        font.dispose()
        stage.dispose()
    }

    fun onResize(width: Float, height: Float) {
        uiViewport.update(width.toInt(), height.toInt(), true)
    }
}
