package dev.lionk.infojump

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Game
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.physics.box2d.Box2D
import dev.lionk.infojump.rendering.TextureManager
import dev.lionk.infojump.views.AbstractView
import dev.lionk.infojump.views.GameView
import dev.lionk.infojump.views.MenuView

class Main : ApplicationAdapter() {
    private lateinit var currentView: AbstractView

    init {
        INSTANCE = this
    }

    override fun create() {
        Box2D.init()

        TextureManager.loadTextures()
        currentView = MenuView(){
            println("Initializing Game")
            changeView("game")
        }
    }

    fun changeView(view: String){
        val tmp = currentView;
        currentView = when(view.trim()){
            "game" ->  GameView()
            "menu" ->  MenuView(){
                println("Initializing Game")
                changeView("game")
            }
            else ->  MenuView()
        }
        currentView.onResize(width, height)

        tmp?.dispose()
    }

    override fun render() {
        currentView.handleInput()
        currentView.render()
    }

    private var height: Int = 0
    private var width: Int = 0

    override fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
        currentView.onResize(width, height)
    }

    companion object{
        lateinit var INSTANCE: Main
            private set

    }
}
