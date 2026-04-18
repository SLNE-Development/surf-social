package dev.slne.surf.social.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.social.core.client.ClientSocialInstance
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override suspend fun onLoadAsync() {
        ClientSocialInstance.clientLoader.onLoad()
    }

    override suspend fun onDisableAsync() {
        ClientSocialInstance.clientLoader.onDisable()
    }
}