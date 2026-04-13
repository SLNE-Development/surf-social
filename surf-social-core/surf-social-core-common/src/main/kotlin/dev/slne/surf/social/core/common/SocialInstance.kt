package dev.slne.surf.social.core.common

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.rabbitmq.api.RabbitMQApi

private val instance = requiredService<SocialInstance>()

interface SocialInstance {
    val rabbitApi: RabbitMQApi

    companion object : SocialInstance by instance {
        val INSTANCE get() = instance
    }
}