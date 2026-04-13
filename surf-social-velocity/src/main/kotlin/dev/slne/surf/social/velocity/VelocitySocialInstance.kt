package dev.slne.surf.social.velocity

import com.google.auto.service.AutoService
import dev.slne.surf.social.core.client.ClientLoader
import dev.slne.surf.social.core.client.ClientSocialInstance
import dev.slne.surf.social.core.common.SocialInstance

@AutoService(SocialInstance::class)
class VelocitySocialInstance : ClientSocialInstance {
    override val clientLoader: ClientLoader = ClientLoader(plugin.dataPath)
}
