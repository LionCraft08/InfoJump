package dev.lionk.infojump

import java.sql.Date
import kotlin.time.Clock
import kotlin.time.Instant

object LionLog {
    fun debug(msg: String){
        info(LogChannel.Debug, msg)
    }
    fun info(msg: String){
        info(LogChannel.Info, msg)
    }
    fun server(msg: String){
        info(LogChannel.Server, msg)
    }
    fun client(msg: String){
        info(LogChannel.Client, msg)
    }
    fun info(channel: LogChannel, msg: String){
        println("${channel.prefix} >> $msg")
    }
    fun warn(msg: String){
        info(LogChannel.Error, msg)
    }
}
enum class LogChannel(
    val prefix: String
){
    Debug("DEBUG"),
    Info("GENERAL"),
    Server("SERVER"),
    Client("Serververbindung"),
    Error("FEHLER"),
    WebSocket("Socket-Verbindung")
}
