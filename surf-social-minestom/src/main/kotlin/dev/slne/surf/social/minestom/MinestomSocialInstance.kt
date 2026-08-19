package dev.slne.surf.social.minestom

import com.google.auto.service.AutoService
import dev.slne.surf.social.core.client.ClientLoader
import dev.slne.surf.social.core.client.ClientSocialInstance
import dev.slne.surf.social.core.common.SocialInstance

@AutoService(SocialInstance::class)
class MinestomSocialInstance : ClientSocialInstance {
    override val clientLoader = ClientLoader(SocialMinestomEntrypoint.dataPath)
}
