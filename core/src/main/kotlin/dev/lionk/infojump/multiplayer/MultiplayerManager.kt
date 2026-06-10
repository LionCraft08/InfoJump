package dev.lionk.infojump.multiplayer

import com.badlogic.gdx.graphics.Color
import dev.lionk.infojump.LionLog
import dev.lionk.infojump.Main
import dev.lionk.infojump.data.Settings
import dev.lionk.infojump.game.GameManager
import dev.lionk.infojump.payloads.EndGamePayload
import dev.lionk.infojump.payloads.HandshakePayload
import dev.lionk.infojump.payloads.LionDeserialization
import dev.lionk.infojump.payloads.LoginPayload
import dev.lionk.infojump.payloads.Player
import dev.lionk.infojump.payloads.PlayerListUpdatePayload
import dev.lionk.infojump.payloads.PlayerUpdatePayload
import dev.lionk.infojump.payloads.ReadyPayload
import dev.lionk.infojump.payloads.ServerAssetsSendPayload
import dev.lionk.infojump.payloads.StartGamePayload
import dev.lionk.infojump.tick.TickManager
import dev.lionk.infojump.tick.TickQueue
import dev.lionk.infojump.views.GameView
import dev.lionk.infojump.views.MultiplayerView

object MultiplayerManager {
    var name: String? = null
        private set
    private var tcpConnection: GameClient = GameClient()

    private val serverAssets = mutableMapOf<String, String>()
    private val players = mutableListOf<Player>()

    fun connect(
        ip:String,
        port:String,
        name:String,
    ): Boolean{
        return try {
            tcpConnection.connect(ip, port.toInt(), LoginPayload(name))
            this.name = name
            true
        }catch (e:Exception){
            handleError(e)
            false
        }

    }

    fun getPlayers():List<Player>{
        return players
    }

    fun sendReadyCheck(){
        tcpConnection.sendData(LionDeserialization.serialize(ReadyPayload(
            name?:""
        )))
    }

    fun isInMultiplayer(): Boolean{
        return tcpConnection.isConnected()
    }

    fun handleIncomingMessage(message:String?){
        if(message.isNullOrBlank()) return
        val payload = try {
             LionDeserialization.deserialize(message)
        }catch (e:Exception){
            LionLog.debug("Lion connect error: $message")
            e.printStackTrace()
            return
        }
        when (payload){
            is HandshakePayload -> {
                val view = Main.INSTANCE.getView() as? MultiplayerView
                view?.handleHandshake(payload)
            }
            is PlayerListUpdatePayload -> {
                val view = Main.INSTANCE.getView() as? MultiplayerView
                view?.handlePlayerListUpdate(payload)
                players.clear()
                players.addAll(payload.list)
                val color = players.find { it.name == name }?.color
                if(color != null)
                    Settings.playerColor = Color(color.toInt())
            }
            is ServerAssetsSendPayload -> {
                serverAssets.clear()
                serverAssets.putAll(payload.assets)
            }
            is StartGamePayload -> {
                TickQueue.addFunction {
                    startGame(payload.gameAssetKey)
                }
            }
            is PlayerUpdatePayload -> {
                val view = Main.INSTANCE.getView() as? GameView
                view?.multiplayerGameAddon?.updatePlayerPos(payload.player, payload.x, payload.y)
            }
            is EndGamePayload -> {
                TickQueue.addFunction {
                    GameManager.endGame()
                }
            }
        }
    }

    fun getAsset(key: String): String {
        //LionLog.debug("requesting asset $key: ${serverAssets[key]}")
        return serverAssets[key]?:""
    }

    fun sendPositionUpdate(
        posX:Float,posY:Float,
    ){
        tcpConnection.sendData(
            LionDeserialization.serialize(
                PlayerUpdatePayload(
                    posX,posY,
                    name!!
                )
            )
        )
    }

    fun startGame(game:String){
        Main.INSTANCE.changeView("multiplayer_game:${game}")
        TickManager.start()

    }

    fun stopGame(){
        TickManager.stop()
    }


    private fun handleError(e: Exception){
        name = null
        LionLog.client("Verbindung konnte nicht aufgebaut werden: ${e.message}")
    }
}
