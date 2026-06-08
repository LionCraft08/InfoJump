@file:JvmName("ServerLauncher")

package dev.lionk.infojump.server

import dev.lionk.infojump.LionLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val server: WebSocketServer = WebSocketServer(6789)


suspend fun main() {
    LionLog.server("Loading assets")
    GameFileManager.ensureLoaded()
    withContext(Dispatchers.Default){
        this.launch {
            ConsoleReader.startConsoleIn()
        }
        this.launch{
            LionLog.server("Attempting to boot TCP server")
            server.start()
        }
    }
    LionLog.server("Server shut down")
}
