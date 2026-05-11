package dev.lionk.infojump.rendering

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider
import com.badlogic.gdx.graphics.glutils.FileTextureData
import java.io.File

object TextureManager {
    private val textures = mutableMapOf<String, Texture>()
    private fun addTexture(name:String){
        textures[name] = Texture(loadAsset(name, "png", "jpg", "jpeg"))
    }

    fun loadAsset(name:String, vararg filetypes: String): FileHandle{
        val path = "assets\\"+name.replace(".", "\\")
        var file = File(path)
        if(!file.exists()){
            for(filetype in filetypes){
                file = File("$path.$filetype")
                if(file.exists()){
                    break
                }
            }
        }
        if(!file.exists()){
            System.err.println(file.absolutePath)
            throw IllegalArgumentException("Datei $name existiert nicht")
        }
        return FileHandle(file)
    }


    fun getTexture(name: String): Texture = textures.get(name) ?: throw IllegalArgumentException("Textur $name ist nicht geladen")
    fun loadTextures(){
        addTexture("game.player.ninja")
        addTexture("game.player.skelett")
        addTexture("game.env.background")
        addTexture("game.objects.muenze")
    }

}
