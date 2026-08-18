package dev.slne.surf.social.microservice.rpc

import dev.slne.surf.social.core.common.rabbit.rpc.SocialConnectionRpc
import dev.slne.surf.social.microservice.repository.AccountsRepository
import java.util.*

class SocialConnectionRpcImpl : SocialConnectionRpc {
    override suspend fun unlinkMinecraftAccount(minecraftUuid: UUID): SocialConnectionRpc.UnlinkResult {
        return AccountsRepository.unlinkMinecraftAccountWithResult(minecraftUuid)
    }
}