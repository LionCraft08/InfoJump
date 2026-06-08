package dev.lionk.infojump.server

import dev.lionk.infojump.payloads.LionDeserialization
import dev.lionk.infojump.payloads.LoginPayload
import dev.lionk.infojump.payloads.PlayerUpdatePayload
import dev.lionk.infojump.payloads.ReadyPayload

object PayloadManager {
    fun handleMessage(address: String, data: String){
        when(val wrapper = LionDeserialization.deserialize(data)) {
            is LoginPayload -> {
                GameManager.addPlayer(address, wrapper.username)
            }
            is PlayerUpdatePayload -> {
                server.send(data, listOf())
            }
            is ReadyPayload -> {
                GameManager.markPlayerAsReady(address)
            }
        }
    }
}
