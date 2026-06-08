package dev.lionk.infojump.data

import com.badlogic.gdx.graphics.Color
import dev.lionk.infojump.utils.CommonData

object Settings {
    var isDebugging = false
    var playerColor: Color = Color.PURPLE

    fun getAvailableColors():Map<String, Color>{
        return CommonData.getAvailableColors().mapValues { Color(it.value.toInt()) }
    }
}
