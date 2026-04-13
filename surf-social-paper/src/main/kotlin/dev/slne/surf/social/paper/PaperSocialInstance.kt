package dev.slne.surf.social.paper

import com.google.auto.service.AutoService
import dev.slne.surf.social.core.client.ClientLoader
import dev.slne.surf.social.core.client.ClientSocialInstance
import dev.slne.surf.social.core.common.SocialInstance

@AutoService(SocialInstance::class)
class PaperSocialInstance : ClientSocialInstance {
    override val clientLoader = ClientLoader(plugin.dataPath)
}