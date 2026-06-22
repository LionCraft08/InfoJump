package dev.lionk.infojump.views.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import dev.lionk.infojump.Main
import dev.lionk.infojump.actions.ActionManager
import dev.lionk.infojump.data.Settings
import dev.lionk.infojump.game.GameManager
import dev.lionk.infojump.level.Level
import dev.lionk.infojump.logic.Timer
import dev.lionk.infojump.payloads.PlayerFinishPayload
import dev.lionk.infojump.rendering.TextureManager
import dev.lionk.infojump.views.GameView
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration


private const val SPLASH_SCREEN_TIME = 2500L //wie lange eine Nachricht unten links angezeigt wird in ms

/**
 * Das Interface, das während des Spiels angezeigt wird.
 */
class UI (
    level: Level? = GameManager.getCurrentLevel()
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

    private val health = Table()
    private val times = Table()
    private val deathOverlay = Table()

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

        //Health
        health.setFillParent(true)
        health.debug = Settings.isDebugging
        health.top()
        health.pad(50f)
        health.scaleBy(3f)
        updateHealth()
        stage.addActor(health)


        val wrapper = Container(times)
        wrapper.setFillParent(true)
        wrapper.right()
        wrapper.pad(10f)
        wrapper.debug = Settings.isDebugging
        stage.addActor(wrapper)

        deathOverlay.background(SpriteDrawable(Sprite(TextureManager.getTexture("game.background.death_screen"))))
        deathOverlay.bottom()
        deathOverlay.setFillParent(true)
        deathOverlay.setColor(1f, 1f, 1f, 0f)
        stage.addActor(deathOverlay)

        generator.dispose()

    }

    fun updateHealth(){
        val currentHealth = GameManager.getCurrentLevel().player.getHealth()
        health.clear()
        for (i in 0..2) {
            if (i < currentHealth) {
                health.add(Image(TextureManager.getTexture("game.objects.herz")).apply { setScale(3f) }).pad(16f)
            } else health.add(Image(TextureManager.getTexture("game.objects.herz_weg")).apply { setScale(3f) }).pad(16f)
        }
    }

    private var isFirst = true

    fun handlePlayerFinish(payload: PlayerFinishPayload){
        val style = TextButton.TextButtonStyle(null, null, null, font)
        if(isFirst){
            times.background = NinePatchDrawable(NinePatch(TextureManager.getTexture("ui.buttons.default"), 8, 8, 8, 8))
            times.add(
                TextButton(
                    "Zeiten: ",
                    style,
                )
            )
            times.row()
        }
        isFirst = false

        times.row()
        times.add(
            TextButton(
                payload.player,
                style,
            ).apply {
                pad(5f)
            }
        )
        times.add(
            TextButton(
                getAsFullString(payload.time.toDuration(DurationUnit.MILLISECONDS)),
                style,
            ).apply {
                pad(5f)
            }
        )
    }

    private fun getAsFullString(duration: Duration): String{
        val hours = duration.inWholeHours
        val minutes = duration.inWholeMinutes - hours * 60
        val seconds = duration.inWholeSeconds - duration.inWholeMinutes * 60
        val milliseconds = duration.inWholeMilliseconds - duration.inWholeSeconds * 1000
        return if(hours > 0) "${hours}h " else "" +
            "${minutes}m " +
            "${seconds}s " +
            "${milliseconds}ms"
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

    fun handleFinish(){
        timer.stop()
        displaySplashNotification("Ziel erreicht! Zeit: ${timer.getAsFullString()}")
        splashNotificationSetTime = System.currentTimeMillis() + 5000
    }

    fun displaySplashNotification(notification: String){
        splashNotification = notification
        splashNotificationSetTime = System.currentTimeMillis()
    }


    fun render(deathAnimationStage:Float){
        uiViewport.apply()
        uiBatch.projectionMatrix = uiCamera.combined

        if(deathAnimationStage >= 2f){
            deathOverlay.setColor(1f, 1f, 1f, (3f - deathAnimationStage).coerceIn(0f, 1f))
        }else if(deathAnimationStage >= 0){
            deathOverlay.setColor(1f, 1f, 1f, deathAnimationStage.coerceAtMost(1f))
        }

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
