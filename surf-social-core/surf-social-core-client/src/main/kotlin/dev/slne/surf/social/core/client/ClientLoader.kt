package dev.slne.surf.social.core.client

import dev.slne.surf.rabbitmq.api.ClientRabbitMQApi
import java.nio.file.Path

class ClientLoader(
    dataPath: Path
) {
    val rabbitApi = ClientRabbitMQApi.create("surf-social", dataPath)

    suspend fun onLoad() {
        rabbitApi.freezeAndConnect()
    }

    suspend fun onDisable() {
        rabbitApi.disconnect()
    }
}