package dev.lionk.infojump.data

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import dev.lionk.infojump.utils.CommonData

object Settings {
    var isDebugging = false
    var playerColor: Color = Color.PURPLE
    val keys = mutableMapOf<KeyAction, Int>().apply {
        put(KeyAction.Jump, Keys.UP)
        put(KeyAction.Left, Keys.LEFT)
        put(KeyAction.Right, Keys.RIGHT)
    }

    fun getAvailableColors():Map<String, Color>{
        return CommonData.getAvailableColors().mapValues { Color(it.value.toInt()) }
    }
}

fun Int.toKey():String{
    return when(this){
        Keys.UP -> "Pfeil Oben"
        Keys.DOWN -> "Pfeil Unten"
        Keys.RIGHT -> "Pfeil Rechts"
        Keys.LEFT -> "Pfeil Links"
        Keys.SPACE -> "Leertaste"
        else -> Keys.toString(this)
    }
}

enum class KeyAction(
    val description:String?=null,
){
    Jump("Springen"),
    Left("Links"),
    Right("Rechts");
    fun getFriendlyDescription():String{
        return description ?: name
    }
}
