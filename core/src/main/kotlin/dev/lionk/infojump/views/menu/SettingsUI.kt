package dev.lionk.infojump.views.menu

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.utils.Align
import dev.lionk.infojump.data.Settings
import dev.lionk.infojump.rendering.TextureManager
import dev.lionk.infojump.views.createFont

class SettingsUI(
    val stage: Stage
) {
    val table: Table = Table()
    val font = createFont(32, borderSize = 0.8f)

    init {
        table.setFillParent(false)
        table.right()
        table.debug = Settings.isDebugging
        table.background(NinePatchDrawable(NinePatch(TextureManager.getTexture("ui.buttons.default"), 8, 8, 8, 8)))
        table.pad(20f)
        table.isVisible = false

        val container = Container(table)
        container.setFillParent(true)
        container.right()
        stage.addActor(container)
        addSetting("Debugging", Settings.isDebugging, {Settings.isDebugging = it})
        addSelector("Spieler-Farbe:",
            Settings.getAvailableColors().values.indexOf(Settings.playerColor), Settings.getAvailableColors().mapValues { it.value.toString() }, { Settings.playerColor = Color.valueOf(it) })
    }

    fun toggle(){
        table.isVisible = !table.isVisible
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
