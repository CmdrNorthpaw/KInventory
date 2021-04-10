package uk.cmdrnorthpaw.kinventory.model

import kotlinx.serialization.Serializable
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import uk.cmdrnorthpaw.kinventory.model.SerializableItemStack.Companion.serializable

@Serializable
abstract class SerializableInventory<T: Inventory>(val items: List<SerializableItemStack>) {
    abstract fun toInventory(): T
}