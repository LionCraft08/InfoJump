package dev.lionk.infojump.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import dev.lionk.infojump.Main
import dev.lionk.infojump.data.KeyAction
import dev.lionk.infojump.data.Settings
import dev.lionk.infojump.rendering.TextureManager
import dev.lionk.infojump.views.menu.MenuBackground
import dev.lionk.infojump.views.menu.SettingsUI


/**
 * Das Hauptmenu des Spiels.
 * Beinhaltet Einstellungsmenü
 */
class MenuView(
    val onViewChange: (String)->Unit = {}
): AbstractView(

) {
    var stage: Stage? = null
    val font: BitmapFont

    val background: MenuBackground

    val buttonBackground: NinePatchDrawable
    val buttonBackgroundHover: NinePatchDrawable
    val settingsUI: SettingsUI

    init {

        stage = Stage(ScreenViewport())

        Gdx.input.inputProcessor = stage

        buttonBackground = NinePatchDrawable(NinePatch(
            TextureManager.getTexture("ui.buttons.default"), 8, 8, 8, 8))
        buttonBackgroundHover = NinePatchDrawable(NinePatch(
            TextureManager.getTexture("ui.buttons.default_pressed"), 8, 8, 8, 8))

        //Font generator
        font = createFont(
            size = 32,
            color = Color.WHITE,
            borderSize = 1.3f,
            borderColor = Color.BLACK
        )
        //end

        val table = Table()
        table.setFillParent(false)
        table.setDebug(Settings.isDebugging)
        table.isTransform = true
        table.rotation = 15f
        table.x = 300f
        table.y = 300f
        table.scaleBy(2f)

        background = MenuBackground(stage!!)

        stage!!.addActor(table)

        settingsUI = SettingsUI(stage!!)

        val textStyle = TextButton.TextButtonStyle(null, null, null, font)
        textStyle.overFontColor = Color.SKY
        textStyle.up = buttonBackground
        textStyle.over = buttonBackgroundHover
        val playButton = TextButton("Singleplayer", textStyle)
        val multiplayerButton = TextButton("Multiplayer",textStyle)
        val settingsButton = TextButton("Einstellungen",textStyle)


        //Singleplayer Button
        playButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                onViewChange("game")
            }
        })
        //Einstellungs-Button
        settingsButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                settingsUI.toggle()
            }
        })
        //Multiplayer Button
        multiplayerButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                Main.INSTANCE.changeView("multiplayer")
            }
        })

        //settingsButton.setX(100f, 1)
        //Reihenfolge festlegen
        table.add(playButton).fillX().uniformX().pad(10f)//.actor.background(buttonBackground)
        table.row()
        table.add(multiplayerButton).fillX().uniformX().pad(10f)
        table.row()
        table.add(settingsButton).fillX().uniformX().pad(10f)
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
    }


    override fun handleInput() {

    }

    override fun onResize(width: Int, height: Int) {
        background.onResize(width, height)
        stage?.viewport?.update(width, height, true)

    }
}
