package uk.cmdrnorthpaw.kinventory

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import uk.cmdrnorthpaw.kinventory.inventory.SerializablePlayerInventory.SerializableServerPlayerInventory

object KInventory : ModInitializer {
    override fun onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(SerializableServerPlayerInventory.Companion::onStart)
    }


    fun Config(config: KInventoryConfig.() -> Unit) {
        val newConfig = KInventoryConfig()
        config(newConfig)
        this.Config = newConfig
    }
    var Config: KInventoryConfig = KInventoryConfig()
        private set
}