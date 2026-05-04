package dev.lionk.infojump.rendering

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider
import com.badlogic.gdx.graphics.glutils.FileTextureData
import java.io.File

object TextureManager {
    private val textures = mutableMapOf<String, Texture>()
    private fun addTexture(name:String){
        val path = name.replace(".", "/")
        var file = File(path)
        if(!file.exists()){
            file = File("$path.png")
        }
        if(!file.exists()){
            file = File("$path.jpg")
        }
        if(!file.exists()){
            file = File("$path.jpeg")
        }
        if(!file.exists()){
            throw IllegalArgumentException("Texture $name existiert nicht")
        }
        textures[name] = Texture(FileHandle(file))
    }


    fun getTexture(name: String): Texture = textures.get(name) ?: throw IllegalArgumentException("Textur $name ist nicht geladen")
    fun loadTextures(){
        addTexture("game.player.static")
        addTexture("game.env.block.default")
    }

}
