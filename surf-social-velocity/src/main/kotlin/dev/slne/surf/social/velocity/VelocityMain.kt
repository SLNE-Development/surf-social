package dev.slne.surf.social.velocity

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.slne.surf.social.core.client.ClientSocialInstance
import dev.slne.surf.social.velocity.command.linkCommand
import dev.slne.surf.social.velocity.command.surfSocialCommand
import dev.slne.surf.social.velocity.config.SocialConfigManager
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import java.nio.file.Path

class VelocityMain @Inject constructor(
    val proxy: ProxyServer,
    @param:DataDirectory val dataPath: Path,
    val logger: Logger,
    suspendingPluginContainer: SuspendingPluginContainer
) {
    init {
        suspendingPluginContainer.initialize(this)
        instance = this

        runBlocking {
            ClientSocialInstance.clientLoader.onLoad()
        }
    }

    @Subscribe
    fun onProxyInitialize(event: ProxyInitializeEvent) {
        surfSocialCommand()
        linkCommand()
    }

    @Subscribe
    fun onProxyShutdown(event: ProxyShutdownEvent) {
        client.close()

        runBlocking {
            ClientSocialInstance.clientLoader.onDisable()
        }
    }

    companion object {
        lateinit var instance: VelocityMain
    }

    val socialConfigManager = SocialConfigManager()
}

val client = HttpClient {
    install(ContentNegotiation) {
        json()
    }
}

val config get() = plugin.socialConfigManager.config
val plugin get() = VelocityMain.instance
