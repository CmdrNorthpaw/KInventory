package uk.cmdrnorthpaw.kinventory.model

import kotlinx.serialization.Serializable
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.StringNbtReader
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

@Serializable
open class SerializableItemStack(
    val item: String,
    val count: Int,
    val nbt: String? = null,
) {
    fun toItemStack(): ItemStack {
        val stack = ItemStack(Registry.ITEM.get(Identifier.tryParse(item)), count)
        if (nbt != null && nbt != "null") stack.tag = StringNbtReader.parse(nbt)
        return stack
    }

    companion object {
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