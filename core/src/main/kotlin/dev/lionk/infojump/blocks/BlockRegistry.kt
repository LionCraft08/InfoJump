package dev.lionk.infojump.blocks

import com.badlogic.gdx.files.FileHandle
import com.google.gson.Gson

object BlockRegistry {
    private val gson = Gson()
    fun loadBlocks(){
        val blockDefs = loadBlockJsons(FileHandle("game\\block\\defs"))

    }

    fun loadBlockJsons(folder:FileHandle):List<String>{
        val list = mutableListOf<String>()
        if(folder.isDirectory){
            folder.list().forEach { file ->
                list.addAll(loadBlockJsons(file))
            }
        }else list.add(folder.readString())
        return list
    }
}
