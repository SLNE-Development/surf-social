package dev.slne.surf.social.bukkit.view

import dev.slne.surf.surfapi.bukkit.api.inventory.framework.titleBuilder
import me.devnatan.inventoryframework.View
import me.devnatan.inventoryframework.ViewConfigBuilder
import me.devnatan.inventoryframework.ViewType
import me.devnatan.inventoryframework.context.OpenContext

class ProfileView : View() {
    override fun onInit(config: ViewConfigBuilder) {
        config.titleBuilder {
            primary("Profil")
        }
        config.type(ViewType.CHEST)
        config.size(5)
        config.cancelInteractions()
    }

    override fun onOpen(open: OpenContext) {
        
    }
}