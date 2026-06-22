package dev.lionk.infojump.multiplayer

import dev.lionk.infojump.LionLog
import dev.lionk.infojump.Main
import dev.lionk.infojump.game.Game
import dev.lionk.infojump.payloads.LionDeserialization
import dev.lionk.infojump.payloads.LoginPayload
import dev.lionk.infojump.payloads.Payload
import dev.lionk.infojump.views.ConnectionStage
import dev.lionk.infojump.views.MultiplayerView
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

/**
 * TCP Client für Multiplayer-Verbindung
 */
class GameClient {
    private var socket: Socket? = null
    private var out: PrintWriter? = null
    private var `in`: BufferedReader? = null

    fun connect(host: String?, port: Int, setup: LoginPayload) {
        Thread {
            try {
                socket = Socket(host, port)
                out = PrintWriter(socket!!.getOutputStream(), true)
                `in` = BufferedReader(InputStreamReader(socket!!.getInputStream()))

                LionLog.client("Erfolgreich verbunden mit ${socket!!.inetAddress}:${socket!!.port}")
                val view = Main.INSTANCE.getView() as? MultiplayerView
                view?.connectionStage = ConnectionStage.WaitingForHandshake

                out!!.println(LionDeserialization.serialize(setup))


                var serverMessage: String?
                while ((`in`!!.readLine().also { serverMessage = it }) != null) {
                    //LionLog.client("Server: $serverMessage")
                    MultiplayerManager.handleIncomingMessage(serverMessage)
                }
            } catch (e: Exception) {
                val view = Main.INSTANCE.getView() as? MultiplayerView
                view?.connectionStage = ConnectionStage.Error
                e.printStackTrace()
            }
        }.start()
    }

    fun isConnected(): Boolean{
        return socket?.isConnected?:false && (!(socket?.isClosed?:true))
    }

    fun sendData(payload: String) {
        if (out != null) {
            out!!.println(payload)
        }else LionLog.warn("Nachricht konnte nicht an Server gesendet werden.")
    }
}
