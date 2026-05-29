package dev.lionk.infojump.views

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import dev.lionk.infojump.rendering.TextureManager

fun createFont(
    size: Int = 32,
    color: Color = Color.WHITE,
    borderSize: Float = 1.3f,
    borderColor: Color = Color.BLACK,
): BitmapFont {
    val generator = FreeTypeFontGenerator(TextureManager.loadAsset("ui/pixelfont", "ttf"))
    val parameter: FreeTypeFontGenerator.FreeTypeFontParameter = FreeTypeFontGenerator.FreeTypeFontParameter()
    parameter.size = size
    parameter.color = color
    parameter.borderWidth = borderSize
    parameter.borderColor = borderColor

    val font = generator.generateFont(parameter)

    generator.dispose()

    return font
}
