package dev.lionk.infojump.payloads

import kotlinx.serialization.Serializable

@Serializable
sealed interface Payload

@Serializable
data class LoginPayload(val username: String) : Payload

@Serializable
data class PlayerUpdatePayload(
    val x: Float, val y: Float,
    val currentLevel:Int?=null,
    val player: String) : Payload

@Serializable
data class ReadyPayload(
    val name: String,
) : Payload

@Serializable
data class StartGamePayload(
    val countdown:Int?,
    val gameAssetKey: String="multiplayer_game",
): Payload

@Serializable
data class PlayerFinishNotificationPayload(
    val player:String,
)


@Serializable
data class PlayerFinishPayload(
    val player:String,
    val time:Long,
)

@Serializable
data class EndGamePayload(
    val successful:Boolean,
)

@Serializable
data class HandshakePayload(
    val success: Boolean,
    val players: List<Player>,
) : Payload
@Serializable
data class ConnectionErrorPayload(
    val errorMessage: String,
    val critical: Boolean,
)
@Serializable
data class Player(
    val name: String,
    val character: String,
    val color: Long,
    var ready: Boolean,
    var isFinished:Boolean = false,
    ) : Payload
@Serializable
data class PlayerListUpdatePayload(
    val list: List<Player>,
) : Payload

@Serializable
data class ServerAssetsSendPayload(
    val assets: Map<String, String>,
): Payload
