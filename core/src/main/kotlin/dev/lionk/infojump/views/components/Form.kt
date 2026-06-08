package dev.lionk.infojump.views.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.ui.Value
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import dev.lionk.infojump.data.Settings
import dev.lionk.infojump.rendering.TextureManager


class InputField(
    headerText: String,
    preview: String,
    font: BitmapFont,
){
    val table = Table()
    val input: TextField

    init {
        table.setDebug(Settings.isDebugging)
        val textStyle = TextButton.TextButtonStyle(null, null, null, font)
        val header = TextButton(headerText, textStyle)
        val inputStyle = TextField.TextFieldStyle(font, Color.WHITE, SpriteDrawable(Sprite(TextureManager.getTexture("ui.cursor.cursor"))), null, null)
        input = TextField("", inputStyle)
        input.messageText = preview
        table.add(header).fillX().uniformX().pad(15f, 5f, 1f, 5f)
        table.row()
        table.add(input).fillX().uniformX().pad(1f, 5f, 15f, 5f)
    }

    fun getActor() = table
    fun getText() = input.text

}

class TextButton(

){

}
