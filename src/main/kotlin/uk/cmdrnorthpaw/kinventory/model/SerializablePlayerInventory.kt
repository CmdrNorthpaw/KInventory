package uk.cmdrnorthpaw.kinventory.model

import kotlinx.serialization.Serializable
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry
import uk.cmdrnorthpaw.kinventory.model.SerializableItemStack.Companion.serializable

@Serializable
class SerializablePlayerInventory (
    val items: Array<SerializableItemStack>,
    val armour: Array<SerializableArmourPiece>,
    val offHand: SerializableItemStack,
    val xp: Int
) {
    fun toInventory(player: PlayerEntity): PlayerInventory {
        val inventory = PlayerInventory(player)
        items.map { it.toItemStack() }.forEachIndexed { index, itemStack ->
            inventory.main.set(index, itemStack)
        }
        armour.map { it.toItemStack() }.forEachIndexed { index, itemStack ->
            inventory.main.set(index, itemStack)
        }

        return inventory
    }

    fun restoreInventory(player: PlayerEntity) {
        player.totalExperience = xp

        player.inventory.main.clear()
        items.map { it.toItemStack() }.forEachIndexed { index, stack ->
            player.inventory.main.set(index, stack)
        }

        player.inventory.armor.clear()
        armour.map { it.toItemStack() }.forEachIndexed { index, itemStack ->
            player.inventory.armor.set(index, itemStack)
        }
    }

    companion object {
        @JvmStatic
        fun PlayerInventory.serializable(): SerializablePlayerInventory {
            val items = mutableListOf<SerializableItemStack>()
            val armour = mutableListOf<SerializableArmourPiece>()

            this.main.forEach { items.add(SerializableItemStack(getKey(it), it.count, it.tag.toString())) }
            this.armor.forEach {
                val armourPiece: ArmorItem
                if (it.item !is ArmorItem) return@forEach else armourPiece = it.item as ArmorItem
                armour.add(SerializableArmourPiece(getKey(it), it.tag.toString(), armourPiece.slotType))
            }

            return SerializablePlayerInventory(items.toTypedArray(), armour.toTypedArray(), this.offHand[0].serializable(), this.player.totalExperience)
        }

        fun PlayerEntity.restoreInventory(inventory: SerializablePlayerInventory) = inventory.restoreInventory(this)

        private fun getKey(item: ItemStack) = Registry.ITEM.getId(item.item).toString()
    }
}