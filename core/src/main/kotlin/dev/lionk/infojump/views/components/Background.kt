package dev.lionk.infojump.views.components

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import kotlin.math.abs

class Background(
    private val cloudTextures: Array<TextureRegion>?,
    viewportWidth: Float,
    viewportHeight: Float
) {
    private class Cloud {
        var texture: TextureRegion? = null
        var x: Float = 0f
        var y: Float = 0f
        var speed: Float = 0f
    }

    private val activeClouds: Array<Cloud>
    private val parallaxCamera: OrthographicCamera

    private var spawnTimer = 0f
    private var nextSpawnTime = 0f

    var minSpawnDelay: Float = 3f // Seconds
    var maxSpawnDelay: Float = 5.0f
    var minSpeed: Float = 2f // Pixels per second
    var maxSpeed: Float = 6f
    var skyTopPadding: Float = 65f // Abstand von oben

    init {
        this.activeClouds = Array<Cloud>()

        this.parallaxCamera = OrthographicCamera(viewportWidth, viewportHeight)

        scheduleNextSpawn()
    }

    fun updateAndDraw(delta: Float, batch: SpriteBatch, mainCamera: OrthographicCamera) {
        parallaxCamera.position.set(mainCamera.position.x * 0.5f, mainCamera.position.y * 0.5f, 0f)
        parallaxCamera.update()

        // Wolken Spawnen
        spawnTimer += delta
        if (spawnTimer >= nextSpawnTime) {
            spawnCloud()
            spawnTimer = 0f
            scheduleNextSpawn()
        }

        batch.setProjectionMatrix(parallaxCamera.combined)
        batch.begin()
        batch.setColor(1f, 1f, 1f, 1f)

        for (i in activeClouds.size - 1 downTo 0) {
            val cloud = activeClouds.get(i)

            cloud.x += cloud.speed * delta

            batch.draw(cloud.texture, cloud.x, cloud.y, cloud.texture!!.regionWidth.toFloat()/2, cloud.texture!!.regionHeight.toFloat()/2)

            val rightCameraEdge = parallaxCamera.position.x + (parallaxCamera.viewportWidth / 2f)
            if (cloud.x > (rightCameraEdge+50)) {
                activeClouds.removeIndex(i)
            }
        }

        batch.end()
    }

    private fun spawnCloud() {
        if (cloudTextures == null || cloudTextures.isEmpty()) return

        val leftCameraEdge = parallaxCamera.position.x - (parallaxCamera.viewportWidth / 2f)

        val texture = cloudTextures.random()

        val cloudX = leftCameraEdge - texture.getRegionWidth()

        spawnCloud(cloudX, null, texture)

    }

    fun spawnCloud(x: Float, y: Float?=null, texture: TextureRegion?=null){
        if (cloudTextures == null || cloudTextures.isEmpty) return

        val cloud = Cloud()
        cloud.texture = texture?:cloudTextures.random()
        cloud.speed = MathUtils.random(minSpeed, maxSpeed)
        cloud.x = x

        val cloudY = y?: run {
            val topCameraEdge1 =
                this@Background.parallaxCamera.position.y + (this@Background.parallaxCamera.viewportHeight / 2f)
            val minY = topCameraEdge1 - this@Background.skyTopPadding
            val maxY = topCameraEdge1 - cloud.texture!!.getRegionHeight()
            MathUtils.random(minY, maxY)
        }
        cloud.y = cloudY
        activeClouds.add(cloud)
    }

    fun handleCameraMove(currentX: Float, targetX: Float,) {
        if (cloudTextures == null || cloudTextures.isEmpty) return

        val isLeft: Boolean = targetX < currentX
        val cameraMoveDistance = abs(abs(currentX) - abs(targetX))
        val amount = cameraMoveDistance/60*((minSpawnDelay + maxSpawnDelay)/2)
        for (i in 0..amount.toInt()){
            val x = if(isLeft) MathUtils.random(currentX, currentX - cameraMoveDistance)
                    else MathUtils.random(currentX, currentX + cameraMoveDistance)
            val texture = cloudTextures.random()
            var additionalOffset = parallaxCamera.viewportWidth/2
            if(isLeft) additionalOffset += texture.regionWidth
            spawnCloud(x = x + (additionalOffset*(if(isLeft) -1 else 1)))
        }
    }

    private fun scheduleNextSpawn() {
        nextSpawnTime = MathUtils.random(minSpawnDelay, maxSpawnDelay)
    }
}
