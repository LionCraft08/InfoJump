package dev.lionk.infojump.views

abstract class AbstractView() {
    abstract fun render(delta: Float)
    abstract fun dispose()
    abstract fun handleInput()

}
