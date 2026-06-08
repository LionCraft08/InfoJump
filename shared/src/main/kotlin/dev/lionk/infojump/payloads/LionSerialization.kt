package dev.lionk.infojump.payloads

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.serializer
import kotlinx.serialization.json.*

object LionDeserialization {
    val registry = mutableMapOf<String, DeserializationStrategy<Any>>()

    init {
        registerType<LoginPayload>()
        registerType<PlayerUpdatePayload>()
        registerType<HandshakePayload>()
        registerType<Player>()
        registerType<PlayerListUpdatePayload>()
        registerType<ServerAssetsSendPayload>()
        registerType<ReadyPayload>()
        registerType<StartGamePayload>()
    }

    val jsonFormat: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }


    inline fun <reified T : Any> registerType(typeName: String = T::class.simpleName ?: "Unknown") {
        registry[typeName] = serializer<T>()
    }

    inline fun <reified T : Any> serialize(
        payload: T,
        typeName: String = T::class.simpleName ?: "Unknown"
    ): String {
        // Build the wrapper dynamically
        val jsonObject = buildJsonObject {
            put("type", typeName)
            put("payload", jsonFormat.encodeToJsonElement(payload))
        }
        return jsonObject.toString()
    }

    fun deserialize(jsonString: String): Any {
        // 1. Parse the string into a generic JSON tree
        val jsonTree = jsonFormat.parseToJsonElement(jsonString).jsonObject

        // 2. Extract the 'type' string
        val type = jsonTree["type"]?.jsonPrimitive?.content
            ?: throw IllegalArgumentException("Invalid TCP message: Missing 'type' field.")

        // 3. Extract the 'payload' object
        val payloadElement = jsonTree["payload"]
            ?: throw IllegalArgumentException("Invalid TCP message: Missing 'payload' field.")

        // 4. Look up the registered deserializer for this type
        val deserializationStrategy = registry[type]
            ?: throw IllegalArgumentException("Unknown TCP message type received: $type")

        // 5. Decode and return the concrete data class instance
        return jsonFormat.decodeFromJsonElement(deserializationStrategy, payloadElement)
    }
}
