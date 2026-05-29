package dev.lionk.infojump.blocks

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils

class BlockTexture(
    val texture: Texture,
    var blockWidth: Float? = null,
    var blockHeight: Float = 10f,
    val fitSize: Boolean = false,
    var textureTileWidth: Float? = 8f
) {
    private val sprite: Sprite

    init {
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)

        if (blockWidth == null) {
            blockWidth = texture.width * (blockHeight / texture.height)
        }


        val actualTextureTileWidth = textureTileWidth ?: texture.width.toFloat()
        val actualTextureTileHeight = actualTextureTileWidth / texture.width * texture.height

        sprite = Sprite(texture)
        sprite.setSize(blockWidth!!, blockHeight)

        if (!fitSize) {

            sprite.setU2(blockWidth!! / actualTextureTileWidth)
            sprite.setV2(blockHeight / actualTextureTileHeight)
        }
    }

    fun draw(batch: Batch, x: Float, y: Float, rotation:Float=0f) {
        sprite.setOrigin(sprite.width / 2f, sprite.height / 2f)

        sprite.setPosition(
            x - sprite.width / 2f,
            y - sprite.height / 2f
        )

        sprite.rotation = rotation * MathUtils.radiansToDegrees

        sprite.draw(batch)
    }
}
