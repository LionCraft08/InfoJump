package dev.lionk.infojump.server

import java.util.Scanner

object ConsoleReader {
    suspend fun startConsoleIn(){
        val scanner = Scanner(System.`in`)
        var isRunning = true
        while (isRunning) {
            val line = scanner.nextLine()
            when(line.lowercase().trim()){
                "exit"->{
                    server.stop()
                    isRunning = false
                }
                "startgame"->{
                    GameManager.startGame()
                }
            }
        }
    }
}
