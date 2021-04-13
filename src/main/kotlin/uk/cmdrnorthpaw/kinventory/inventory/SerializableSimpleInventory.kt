package uk.cmdrnorthpaw.kinventory.inventory

import kotlinx.serialization.Serializable
import net.minecraft.inventory.SimpleInventory
import uk.cmdrnorthpaw.kinventory.model.SerializableInventory
import uk.cmdrnorthpaw.kinventory.model.SerializableItemStack

@Serializable
open class SerializableSimpleInventory(
    private val itemList: List<SerializableItemStack>
) : SerializableInventory<SimpleInventory>(itemList) {
    override fun toInventory(): SimpleInventory = SimpleInventory(*items.map { it.toItemStack() }.toTypedArray())
}