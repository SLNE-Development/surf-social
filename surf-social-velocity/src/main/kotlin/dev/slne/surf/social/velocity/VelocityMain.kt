package dev.slne.surf.social.velocity

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.slne.surf.social.core.client.ClientSocialInstance
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
    }

    @Subscribe
    fun onProxyShutdown(event: ProxyShutdownEvent) {
        runBlocking {
            ClientSocialInstance.clientLoader.onDisable()
        }
    }

    companion object {
        lateinit var instance: VelocityMain
    }
}

val plugin get() = VelocityMain.instance
