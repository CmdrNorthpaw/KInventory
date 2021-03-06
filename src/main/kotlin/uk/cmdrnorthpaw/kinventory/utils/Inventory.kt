package uk.cmdrnorthpaw.kinventory.utils

import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack

/** Small function to get all the [ItemStack]s in an [Inventory] */
val Inventory.items: List<ItemStack>
    get() {
        val items = mutableListOf<ItemStack>()

        for (slot in 0 until this.size()) {
            items.add(slot, this.getStack(slot))
        }
        return items
    }