package dev.lionk.infojump.views.menu

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import dev.lionk.infojump.rendering.TextureManager
import kotlin.math.max

private const val IMAGE_DURATION = 10 // Wie lange ein Bild angezeigt wird in sekunden

/**
 * Der Hintergrund für das Haupt-Menu, bestehend aus allen Bildern die im Ordner menu mit dem namen "background" sind
 */
class MenuBackground(
    stage: Stage
) {
    val images: MutableList<Actor> = mutableListOf()

    init {
        val textures = TextureManager.getTextureSet("menu.background")
        println("Loaded ${textures.size} menu background images")
        textures.forEach {
            val actor = Image(it)
            actor.setColor(1f, 1f, 1f, 0f)
            images.add(actor)
            stage.addActor(actor)
        }
    }

    private var renderStage: Float = 0f

    private var hasRemovedPreviousImage = false
    private var isFirst = true

    fun render(delta:Float) {

        //Fade-Transition berechnen
        if(images.size <=1) return
        renderStage += delta
        if(renderStage >= images.size*IMAGE_DURATION) renderStage = 0f
        val currentImage = (renderStage / IMAGE_DURATION).toInt()
        val localRenderStage = renderStage % IMAGE_DURATION
        if(renderStage <= 2 && !isFirst){
            images.first().setColor(1f, 1f, 1f, 1f)
            images.last().setColor(1f, 1f, 1f, 1-(renderStage.coerceAtMost(1f)))
        }else if(localRenderStage<=1){
            images[currentImage].setColor(1f, 1f, 1f, localRenderStage)
            hasRemovedPreviousImage = false
        }else if(localRenderStage <= 2){
            images[currentImage].setColor(1f, 1f, 1f, 1f)
            if(!hasRemovedPreviousImage) {
                images[currentImage.coerceAtLeast(1)-1].setColor(1f, 1f, 1f, 1f)
                hasRemovedPreviousImage = true
            }
        }else if(isFirst) isFirst = false
    }

    /**
     * Funktion, die die Koordinaten und Höhe / Breite des Bildes berechnet,
     * damit es zentriert is und den ganzen Bildschirm bedeckt
     */
    private fun calculateCover(
        parentW: Float, parentH: Float,
        childW: Float, childH: Float
    ):ObjectFitResult {
        val scaleX = parentW / childW
        val scaleY = parentH / childH
        val scale = max(scaleX, scaleY)

        val newWidth = childW * scale
        val newHeight = childH * scale

        val x = (parentW - newWidth) / 2.0f
        val y = (parentH - newHeight) / 2.0f

        return ObjectFitResult(x, y, newWidth, newHeight)
    }

    @JvmRecord
    data class ObjectFitResult(val x: Float, val y: Float, val width: Float, val height: Float)

    fun onResize(width: Int, height: Int) {
        images.forEach {
            val objectFitData = calculateCover(
                width.toFloat(),
                height.toFloat(),
                it.width,
                it.height
            )
            it.setPosition(objectFitData.x, objectFitData.y)
            it.width = objectFitData.width
            it.height = objectFitData.height
        }
    }
}
