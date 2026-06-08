package dev.lionk.infojump.tick

object TickQueue {
    private val functions = mutableListOf<()-> Unit>()
    fun addFunction(function:()->Unit){
        functions.add(function)
    }
    fun executeFunctions(){
        functions.forEach { function -> function() }
        functions.clear()
    }
}
