package dev.lionk.infojump

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Game
import com.badlogic.gdx.physics.box2d.Box2D
import dev.lionk.infojump.rendering.TextureManager
import dev.lionk.infojump.views.AbstractView
import dev.lionk.infojump.views.GameView

class Main : ApplicationAdapter() {
    private lateinit var currentView: AbstractView
    override fun create() {
        Box2D.init()

        TextureManager.loadTextures()
        currentView = GameView()
    }

    override fun render() {
        currentView.handleInput()
        currentView.render()
    }

    override fun resize(width: Int, height: Int) {
        currentView.onResize(width, height)
    }
}
