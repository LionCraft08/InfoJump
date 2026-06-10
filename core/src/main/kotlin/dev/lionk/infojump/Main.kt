package dev.lionk.infojump

import com.badlogic.gdx.Game
import com.badlogic.gdx.physics.box2d.Box2D
import dev.lionk.infojump.game.GameManager
import dev.lionk.infojump.multiplayer.MultiplayerGameAddon
import dev.lionk.infojump.multiplayer.MultiplayerManager
import dev.lionk.infojump.rendering.TextureManager
import dev.lionk.infojump.tick.TickManager
import dev.lionk.infojump.tick.TickQueue
import dev.lionk.infojump.views.AbstractView
import dev.lionk.infojump.views.GameView
import dev.lionk.infojump.views.MenuView
import dev.lionk.infojump.views.MultiplayerView

class Main : Game() {
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

    fun getView(): AbstractView {
        return currentView
    }

    fun changeView(view: String){

        val args = view.substringAfter(":", "")
        val view = view.substringBefore(":")

        val tmp = currentView;
        currentView = when(view.trim()){
            "game" ->  GameView()
            "multiplayer" -> MultiplayerView()
            "multiplayer_game" -> GameView(true, args)
            "menu" ->  MenuView(){
                println("Initializing Game")
                changeView("game")
            }
            else ->  MenuView()
        }
        currentView.onResize(width, height)

        tmp.dispose()
    }

    override fun dispose() {
        super.dispose()
        currentView.dispose()
        TickManager.stop()
    }

    override fun render() {
        TickQueue.executeFunctions()
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
