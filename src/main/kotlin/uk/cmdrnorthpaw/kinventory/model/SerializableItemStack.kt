package uk.cmdrnorthpaw.kinventory.model

import kotlinx.serialization.Serializable
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Item
import net.minecraft.nbt.StringNbtReader
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

/**
 * Represents an [ItemStack] which is [Serializable].
 * @param item: The registry key of the item being serialized
 * @param count: How many items were in the stack
 * @param nbt: A string representation of the [ItemStack]'s NBT tag
 * */
@Serializable
open class SerializableItemStack(
    val item: String,
    val count: Int,
    val nbt: String? = null,
) {

    /**
     * Converts a [SerializableItemStack] to a regular ol' [ItemStack].
     * Preserves item type, NBT and stack count.
     * @return The [ItemStack] representation of this [SerializableItemStack] object.
     * */
    fun toItemStack(): ItemStack {
        val stack = ItemStack(Registry.ITEM.get(Identifier.tryParse(item)), count)
        if (nbt != null && nbt != "null") stack.tag = StringNbtReader.parse(nbt)
        return stack
    }

    companion object {
        /**
         * Converts an [ItemStack] to it's [SerializableItemStack] representation.
         * Can determine between an [ArmorItem] and a regular [Item] but otherwise defaults to [SerializableItemStack].
         * Not recommended if you're using a custom serializable item stack type.
         * @return The [ItemStack] in serializable form.
         * */
        fun ItemStack.serializable() : SerializableItemStack {
            val registryId = Registry.ITEM.getKey(this.item).toString()
            return if (this.item is ArmorItem) SerializableArmourPiece(
                registryId,
                this.tag?.asString(),
            (this.item as ArmorItem).slotType)
            else SerializableItemStack(registryId, this.count, this.tag?.asString())
        }
    }
}