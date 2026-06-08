package dev.lionk.infojump.server

import dev.lionk.infojump.LionLog
import dev.lionk.infojump.payloads.HandshakePayload
import dev.lionk.infojump.payloads.LionDeserialization
import dev.lionk.infojump.payloads.Player
import dev.lionk.infojump.payloads.PlayerListUpdatePayload
import dev.lionk.infojump.payloads.ServerAssetsSendPayload
import dev.lionk.infojump.payloads.StartGamePayload
import dev.lionk.infojump.utils.CommonData

object GameManager {
    //                               Addresse, Spielername
    private val players = mutableMapOf<String, Player>()
    fun addPlayer(address: String, name: String) {
        players[address] = Player(
            name = name,
            color = availableColors[nextColor++ % availableColors.size],
            ready = false
        )
        server.send(LionDeserialization.serialize(HandshakePayload(
            success = true,
            players = players.values.toList()
        )), listOf(address))
        server.send(
            LionDeserialization.serialize(
            ServerAssetsSendPayload(
                GameFileManager.getAssets()
            )), listOf(address)
        )
        server.send(
            LionDeserialization.serialize(PlayerListUpdatePayload(
                players.values.toList()
            )),
            listOf()
        )
        LionLog.server("Player $name connected successfully")
    }

    fun markPlayerAsReady(
        address: String
    ){
        players[address]?.ready = true
        server.send(
            LionDeserialization.serialize(PlayerListUpdatePayload(
                players.values.toList()
            )),
            listOf()
        )
        if(players.size >= 2 && players.values.all { it.ready }){
            startGame()
        }
    }

    fun startGame(){
        LionLog.info("Game starting...")
        server.send(LionDeserialization.serialize(
            StartGamePayload(3)),
            listOf()
        )
    }

    fun removePlayer(address: String) {
        val name = players[address]
        players.remove(address)
        server.send(
            LionDeserialization.serialize(PlayerListUpdatePayload(
                players.values.toList()
            )),
            listOf()
        )
        LionLog.server("Player $name disconnected")
    }
    private val availableColors = CommonData.getAvailableColors().values.toList()
    private var nextColor = 0
}
