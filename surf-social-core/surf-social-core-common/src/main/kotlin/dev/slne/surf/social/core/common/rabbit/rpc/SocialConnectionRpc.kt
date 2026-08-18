package dev.slne.surf.social.core.common.rabbit.rpc

import dev.slne.surf.rabbitmq.api.rpc.RpcService
import kotlinx.serialization.Serializable
import java.util.*

@RpcService
interface SocialConnectionRpc {

    suspend fun unlinkMinecraftAccount(minecraftUuid: UUID): UnlinkResult

    @Serializable
    sealed interface UnlinkResult {
        @Serializable
        data object NotLinked : UnlinkResult

        @Serializable
        data class Unlinked(val discordId: Long?) : UnlinkResult
    }
}