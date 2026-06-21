package dev.lionk.infojump.server

import dev.lionk.infojump.payloads.ConnectionErrorPayload
import dev.lionk.infojump.payloads.LionDeserialization
import dev.lionk.infojump.payloads.LoginPayload
import dev.lionk.infojump.payloads.PlayerFinishNotificationPayload
import dev.lionk.infojump.payloads.PlayerFinishPayload
import dev.lionk.infojump.payloads.PlayerUpdatePayload
import dev.lionk.infojump.payloads.ReadyPayload

object PayloadManager {
    fun handleMessage(address: String, data: String){
        val wrapper =
        try {
            LionDeserialization.deserialize(data)
        }catch (e:Exception){
            e.printStackTrace()
            return
        }
        when(wrapper) {
            is LoginPayload -> {
                if(GameManager.getPlayerByName(wrapper.username) == null){
                    GameManager.addPlayer(address, wrapper.username)
                }else{
                    server.send(
                        LionDeserialization.serialize(ConnectionErrorPayload(
                            "Dieser Nutzername existiert bereits.",
                            true
                        )),listOf(address)
                    )
                }
            }
            is PlayerUpdatePayload -> {
                server.send(data, listOf())
            }
            is PlayerFinishNotificationPayload -> {
                GameManager.getPlayer(address)?.let {
                    if(it.isFinished) return@let
                    it.isFinished = true
                    server.send(
                        LionDeserialization.serialize(PlayerFinishPayload(
                            wrapper.player,
                            GameManager.getCurrentIngameTime()
                        )),
                        listOf()
                    )
                }

            }
            is ReadyPayload -> {
                GameManager.markPlayerAsReady(address)
            }
        }
    }
}
