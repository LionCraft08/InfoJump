package dev.lionk.infojump.views

abstract class AbstractView() {
    abstract fun render()
    abstract fun dispose()
    abstract fun handleInput()
    abstract fun onResize(width: Int, height: Int)

}
