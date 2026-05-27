package dev.lionk.infojump.logic

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.loaders.SkinLoader
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Timer(
) {
    private var duration: Duration = Duration.ZERO
    private var isActive: Boolean = false
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        isActive = false
    }
    fun start() {
        isActive = true
        scope.launch {
            sleep(100)
            while (isActive) {
                duration = duration.plus(100.toDuration(DurationUnit.MILLISECONDS))
                sleep(100)
            }
        }
    }
    fun draw(font: BitmapFont, uiBatch: Batch){
        //val layout = font.draw(uiBatch, getAsString(), 0f, 0f)
        val x = 30f//(Gdx.graphics.width - layout.width) / 2
        val y = Gdx.graphics.height - 50f // Top center

        font.draw(uiBatch, getAsString(), x, y)

    }
    fun stop() {
        isActive = false
    }
    fun getAsString(): String = duration.toString()

}
