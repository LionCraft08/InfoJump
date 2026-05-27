package dev.lionk.infojump.views.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.utils.viewport.ScreenViewport
import dev.lionk.infojump.logic.Timer
import dev.lionk.infojump.rendering.TextureManager


private const val SPLASH_SCREEN_TIME = 2500L

class UI (){
    private val font: BitmapFont
    private var uiBatch: SpriteBatch = SpriteBatch()
    private val uiCamera: OrthographicCamera = OrthographicCamera() // Added for UI camera
    private val uiViewport: ScreenViewport = ScreenViewport(uiCamera)

    val timer = Timer()
    private var splashNotification: String? = null
    private var splashNotificationSetTime: Long=0

    init {
        timer.start()
        val generator = FreeTypeFontGenerator(TextureManager.loadAsset("ui/pixelfont", "ttf"))


        val parameter: FreeTypeFontGenerator.FreeTypeFontParameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 64 // The actual target size in pixels
        parameter.color = Color.WHITE

        parameter.borderWidth = 1f
        parameter.borderColor = Color.BLACK

        font = generator.generateFont(parameter)

        generator.dispose()

    }

    fun displaySplashNotification(notification: String){
        splashNotification = notification
        splashNotificationSetTime = System.currentTimeMillis()
    }


    fun render(){
        uiViewport.apply()
        uiBatch.projectionMatrix = uiCamera.combined

        uiBatch.begin()
        timer.draw(font, uiBatch)
        if(System.currentTimeMillis()-splashNotificationSetTime < SPLASH_SCREEN_TIME){
            font.draw(uiBatch, splashNotification, 10f, 70f)
        }
        uiBatch.end()
    }

    fun dispose() {
        uiBatch.dispose()
        font.dispose()
    }

    fun onResize(width: Float, height: Float) {
        uiViewport.update(width.toInt(), height.toInt(), true)
    }
}
