package uk.cmdrnorthpaw.kinventory.inventory

import kotlinx.serialization.Serializable
import net.minecraft.inventory.SimpleInventory
import uk.cmdrnorthpaw.kinventory.mixins.SimpleInventoryAccessor
import uk.cmdrnorthpaw.kinventory.model.SerializableInventory
import uk.cmdrnorthpaw.kinventory.model.SerializableItemStack
import uk.cmdrnorthpaw.kinventory.model.SerializableItemStack.Companion.serializable

@Serializable
class SerializableSimpleInventory(
    private val itemList: List<SerializableItemStack>
) : SerializableInventory<SimpleInventory>(itemList) {
    override fun toInventory(): SimpleInventory = SimpleInventory(*items.map { it.toItemStack() }.toTypedArray())

    companion object {
        fun SimpleInventory.serializable(): SerializableSimpleInventory {
            val stacks = (this as SimpleInventoryAccessor).stacks
            return SerializableSimpleInventory(stacks.toList().map { it.serializable() })
        }
    }
}