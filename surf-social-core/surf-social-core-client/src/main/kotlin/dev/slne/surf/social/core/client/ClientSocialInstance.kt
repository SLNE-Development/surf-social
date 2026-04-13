package dev.slne.surf.social.core.client

import dev.slne.surf.rabbitmq.api.ClientRabbitMQApi
import dev.slne.surf.social.core.common.SocialInstance

interface ClientSocialInstance : SocialInstance {
    val clientLoader: ClientLoader

    override val rabbitApi: ClientRabbitMQApi get() = clientLoader.rabbitApi

    companion object : ClientSocialInstance by SocialInstance.INSTANCE as ClientSocialInstance {
        val INSTANCE get() = SocialInstance.INSTANCE as ClientSocialInstance
    }
}