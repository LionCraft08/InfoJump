package dev.lionk.infojump.data

import com.badlogic.gdx.graphics.Color

object Settings {
    var isDebugging = false
    var playerColor: Color = Color.PURPLE


    private val colors = mutableMapOf<String, Color>().apply{
        put("Lila", Color.PURPLE)
        put("Grün", Color.GREEN)
        put("Golden", Color.GOLD)
        put("Rot", Color.RED)
        put("Himmelblau", Color.SKY)
        put("Blau", Color.BLUE)
        put("Hellgrün", Color.LIME)
        put("Gelb", Color.YELLOW)

    }
    fun getAvailableColors():Map<String, Color>{
        return colors
    }
}
