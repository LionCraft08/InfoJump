package dev.lionk.infojump.views.menu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import dev.lionk.infojump.data.KeyAction
import dev.lionk.infojump.data.Settings
import dev.lionk.infojump.data.toKey
import dev.lionk.infojump.rendering.TextureManager
import dev.lionk.infojump.views.createFont

/**
 * Die EInstellungen im Haupt-Menü
 */
class SettingsUI(
    val stage: Stage
) {
    val table: Table = Table()
    val font = createFont(32, borderSize = 0.8f)
    val listeningForInput = Table()


    init {
        table.setFillParent(false)
        table.right()
        table.debug = Settings.isDebugging
        val defBackground = NinePatchDrawable(NinePatch(TextureManager.getTexture("ui.buttons.default"), 8, 8, 8, 8))
        table.background(defBackground)
        table.pad(20f)
        table.isVisible = false

        val container = Container(table)
        container.setFillParent(true)
        container.right()
        stage.addActor(container)
        table.row()
        addSetting("Debugging", Settings.isDebugging, {Settings.isDebugging = it})
        addSelector("Spieler-Farbe:",
            Settings.getAvailableColors().values.indexOf(Settings.playerColor), Settings.getAvailableColors().mapValues { it.value.toString() }, { Settings.playerColor = Color.valueOf(it) })
        table.row()
        table.row()

        addKeyPicker(
            KeyAction.Jump,
        )
        addKeyPicker(
            KeyAction.Left,
        )
        addKeyPicker(
            KeyAction.Right,
        )

        listeningForInput.add(
            TextButton("Warte auf Eingabe...", TextButton.TextButtonStyle(null, null, null, font))
        )
        listeningForInput.center()
        listeningForInput.isVisible = false
        listeningForInput.background = defBackground
        listeningForInput.pad(20f)
        val wrapper = Container(listeningForInput)
        wrapper.setFillParent(true)
        wrapper.center()
        stage.addActor(wrapper)
    }


    fun toggle(){
        if(currentKeyToListenFor == null)
            table.isVisible = !table.isVisible
    }

    private var currentKeyToListenFor: KeyAction? = null

    fun enableHandleInput(
        keyToListen: KeyAction,
        onKey:(Int)->Unit
    ) {
        currentKeyToListenFor = keyToListen
        listeningForInput.isVisible = true
        Gdx.input.inputProcessor = object : InputProcessor {
            override fun keyDown(keycode: Int): Boolean {
                if(currentKeyToListenFor != null) {
                    Settings.keys[currentKeyToListenFor!!] = keycode
                    currentKeyToListenFor = null
                    listeningForInput.isVisible = false
                    onKey(keycode)
                    Gdx.input.inputProcessor = stage
                }
                return false
            }

            override fun keyUp(keycode: Int): Boolean { return false }
            override fun keyTyped(character: Char): Boolean { return false }
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean { return false }
            override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean { return false }
            override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean { return false }
            override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean { return false }
            override fun mouseMoved(screenX: Int, screenY: Int): Boolean { return false }
            override fun scrolled(amountX: Float, amountY: Float): Boolean { return false }
        }
    }

    private fun addSetting(description: String, initialValue:Boolean, onClick:(Boolean)-> Unit){
        table.add(TextButton(description,
            TextButton.TextButtonStyle(null, null, null, font)))

        table.add(CheckBox("", CheckBox.CheckBoxStyle(
            SpriteDrawable(Sprite(TextureManager.getTexture("ui.buttons.kein_haken"))),
            SpriteDrawable(Sprite(TextureManager.getTexture("ui.buttons.haken"))),
            font, null
        ))
            .apply {
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        onClick.invoke(isChecked)
                    }
                })
                isChecked = initialValue
            })
        table.row()
    }

    private fun getDescription(keyAction: KeyAction):String{
        return "Taste für ${keyAction.getFriendlyDescription()}: ${Settings.keys[keyAction]?.toKey()}"
    }

    private fun addKeyPicker(keyAction: KeyAction){
        val button = TextButton(getDescription(keyAction),
            TextButton.TextButtonStyle(null, null, null, font))

        button.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                enableHandleInput(keyAction){i->
                    button.setText(getDescription(keyAction))
                }
            }
        })

        table.add(
            button,
        )
        table.row()
    }

    private fun addSelector(description: String, initialValue: Int, values: Map<String, String>, onClick:(String)-> Unit){
        val initValue = if(initialValue < 0 || initialValue >= values.size) 0 else initialValue
        table.add(TextButton(
            "$description  ",
            TextButton.TextButtonStyle(null, null, null, font)))
        val button = TextButton(values.keys.toList()[initValue], TextButton.TextButtonStyle(null, null, null, font))
        button.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val next = (Settings.getAvailableColors().values.indexOf(Settings.playerColor) + 1) % values.size
                val nextColor = values.keys.toList()[next]
                button.setText(nextColor)
                onClick.invoke((values[nextColor]?:"FF0000FF"))
            }
        })
        table.add(button)
        table.row()
    }

    fun render(){

    }
}
