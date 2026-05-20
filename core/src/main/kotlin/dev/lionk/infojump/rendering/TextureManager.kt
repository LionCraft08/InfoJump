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
        println("Loading $file as $name")
        return FileHandle(file)
    }

    fun getTextureSet(name:String): List<Texture>{
        val list = mutableListOf(getTexture(name))
        try {
            var id = 2
            while (true){
                list.add(getTexture("$name$id"))
                id++
            }
        }catch (e:IllegalArgumentException){}
        return list
    }

    fun getTexture(name: String): Texture = textures[name] ?: throw IllegalArgumentException("Textur $name ist nicht geladen")
    fun loadTextures(){
        val availableTextures = loadAssetsRecursively(
            FileHandle("assets\\game"),
            "png", "jpg", "jpeg"
        )

        availableTextures.forEach {
            addTexture(it.pathWithoutExtension()
                .replace("\\", ".")
                .replace("/", ".")
                .replaceFirst("assets.", ""))
        }

//        addTexture("game.player.ninja")
//        addTexture("game.player.skelett")
//        addTexture("game.env.background")
//        addTexture("game.objects.muenze")
        FileHandle("assets\\menu").list().forEach {
            if(it.extension().endsWith("png")){
                addTexture("menu.${it.nameWithoutExtension()}")
            }
        }
    }
    private fun loadAssetsRecursively(file: FileHandle, vararg filetypes: String): List<FileHandle>{
        val list = mutableListOf<FileHandle>()
        if(!file.exists()) return list
        if(file.isDirectory){
            file.list().forEach {
                list.addAll(loadAssetsRecursively(it, *filetypes))
            }
        }else if(filetypes.contains(file.extension())){
            list.add(file)
            println("Loaded ${file.pathWithoutExtension()}")
        }
        return list
    }

}
