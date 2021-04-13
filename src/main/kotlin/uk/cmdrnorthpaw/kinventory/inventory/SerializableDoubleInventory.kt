package uk.cmdrnorthpaw.kinventory.inventory

import kotlinx.serialization.Serializable
import net.minecraft.inventory.DoubleInventory
import uk.cmdrnorthpaw.kinventory.model.SerializableInventory

/**
 * Serializable version of [DoubleInventory].
 * Can be converted to a [DoubleInventory] but not from a [DoubleInventory], because there's no way to tell what
 * the [SerializableInventory] counterpart of an inventory is merely from that inventory.
 * @param first The first [SerializableInventory] in this [DoubleInventory]
 * @param second The second [SerializableInventory] in this [DoubleInventory]
 * */
@Serializable
class SerializableDoubleInventory (
    val first: SerializableInventory<*>,
    val second: SerializableInventory<*>
) : SerializableInventory<DoubleInventory>(first.items + second.items) {
    override fun toInventory(): DoubleInventory = DoubleInventory(first.toInventory(), second.toInventory())
}