package dev.lionk.infojump.views

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.viewport.FitViewport
import dev.lionk.infojump.rendering.TextureManager

class GameView(
    val
): AbstractView() {
    val camera = OrthographicCamera()
    val viewport = FitViewport(128f, 72f, camera)
    val playerSprite = Sprite(TextureManager.getTexture("game.player.static"))
    override fun render(delta: Float) {
        viewport.apply()
        camera.update()
    }

    override fun dispose() {
        TODO("Not yet implemented")
    }

    override fun handleInput() {
        TODO("Not yet implemented")
    }


}
