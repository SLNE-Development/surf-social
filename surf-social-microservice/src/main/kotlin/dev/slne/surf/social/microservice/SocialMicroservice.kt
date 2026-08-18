package dev.slne.surf.social.microservice

import com.google.auto.service.AutoService
import dev.slne.surf.database.DatabaseApi
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.microservice.api.microservice.Microservice
import dev.slne.surf.rabbitmq.api.ServerRabbitMQApi
import dev.slne.surf.social.core.common.rabbit.rpc.SocialConnectionRpc
import dev.slne.surf.social.microservice.config.SocialConfig
import dev.slne.surf.social.microservice.handler.SocialConnectionsHandler
import dev.slne.surf.social.microservice.rpc.SocialConnectionRpcImpl
import dev.slne.surf.social.microservice.table.AccountsTable
import kotlin.io.path.Path

@AutoService(Microservice::class)
class SocialMicroservice : Microservice() {
    override val dataPath = Path("config")
    private val databaseApi = DatabaseApi.create(dataPath)
    private val rabbitApi = ServerRabbitMQApi.create("surf-social", dataPath)

    override suspend fun onBootstrap(args: List<String>) {
        SocialConfig.load(dataPath)

        suspendTransaction {
            SchemaUtils.create(AccountsTable)
        }

        rabbitApi.registerRequestHandler(SocialConnectionsHandler)
        rabbitApi.registerRpcService<SocialConnectionRpc>(SocialConnectionRpcImpl())
        rabbitApi.freezeAndConnect()
    }

    override suspend fun onDisable() {
        rabbitApi.disconnect()
        databaseApi.shutdown()
    }
}