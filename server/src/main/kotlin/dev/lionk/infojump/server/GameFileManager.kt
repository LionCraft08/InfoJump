package dev.lionk.infojump.server

import dev.lionk.infojump.LionLog
import java.io.File

/**
 * Zuständig dafür, die Spieldateien (Level) zu laden
 */
object GameFileManager {

    private val assets = mutableMapOf<String, String>()

    init {
        loadAssetsRecursively(
            File("assets\\game\\"), "json"
        ).forEach {
            assets[it.path.substring(0, it.path.lastIndexOf("."))
                .replace("\\", ".")
                .replace("/", ".")
                .replaceFirst("assets.", "")] = it.readText()
        }
        assets.forEach { (string, string1) ->
            LionLog.debug("Loaded $string")
        }
    }

    fun ensureLoaded(){
        //Dummy Function
        getAssets()
    }

    fun getAssets(): Map<String, String> {
        return assets
    }


    private fun loadAssetsRecursively(file: File, vararg filetypes: String): List<File>{
        val list = mutableListOf<File>()
        if(!file.exists()) return list
        if(file.isDirectory){
            file.listFiles()?.forEach {
                list.addAll(loadAssetsRecursively(it, *filetypes))
            }
        }else if(filetypes.contains(file.extension)){
            list.add(file)
        }
        return list
    }
}
