package uk.cmdrnorthpaw.kinventory

import net.fabricmc.api.ModInitializer

object KInventory : ModInitializer {
    override fun onInitialize() {}

    fun Config(config: KInventoryConfig.() -> Unit) {
        val newConfig = KInventoryConfig()
        config(newConfig)
        this.Config = newConfig
    }
    var Config: KInventoryConfig = KInventoryConfig()
        private set
}