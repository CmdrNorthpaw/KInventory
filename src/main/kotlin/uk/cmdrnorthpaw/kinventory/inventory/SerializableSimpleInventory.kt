package uk.cmdrnorthpaw.kinventory.inventory

import kotlinx.serialization.Serializable
import net.minecraft.inventory.SimpleInventory
import uk.cmdrnorthpaw.kinventory.model.SerializableInventory
import uk.cmdrnorthpaw.kinventory.model.SerializableItemStack
import uk.cmdrnorthpaw.kinventory.model.SerializableItemStack.Companion.serializable
import uk.cmdrnorthpaw.kinventory.serializers.SimpleInventorySerializer
import uk.cmdrnorthpaw.kinventory.utils.items

@Serializable
class SerializableSimpleInventory(
    private val itemList: List<SerializableItemStack>
) : SerializableInventory<@Serializable(SimpleInventorySerializer::class) SimpleInventory>(itemList) {
    override fun toInventory(): SimpleInventory = SimpleInventory(*items.map { it.toItemStack() }.toTypedArray())

    companion object : SerializableInventoryCompanion<SimpleInventory, SerializableSimpleInventory> {
        override fun getSerializable(from: SimpleInventory): SerializableSimpleInventory {
            val stacks = from.items
            return SerializableSimpleInventory(stacks.toList().map { it.serializable() })
        }

        fun SimpleInventory.serializable() = getSerializable(this)
    }
}