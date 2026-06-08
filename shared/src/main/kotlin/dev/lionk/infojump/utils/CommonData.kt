package dev.lionk.infojump.utils

object CommonData {
    private val colors = mutableMapOf<String, Long>().apply{
        put("Lila", 0xa020f0ff)
        put("Grün", 0x00ff00ff)
        put("Golden", 0xffd700ff)
        put("Rot", 0xff0000ff)
        put("Himmelblau", 0x87ceebff)
        put("Blau", 0x0000ffff)
        put("Hellgrün", 0x32cd32ff)
        put("Gelb", 0xffff00ff)
    }
    fun getAvailableColors():Map<String, Long>{
        return colors
    }
}
