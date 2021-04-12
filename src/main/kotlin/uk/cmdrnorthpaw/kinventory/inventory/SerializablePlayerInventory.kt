package uk.cmdrnorthpaw.kinventory.inventory

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.registry.Registry
import uk.cmdrnorthpaw.kinventory.model.SerializableArmourPiece
import uk.cmdrnorthpaw.kinventory.model.SerializableInventory
import uk.cmdrnorthpaw.kinventory.model.SerializableItemStack
import uk.cmdrnorthpaw.kinventory.model.SerializableItemStack.Companion.serializable
import java.util.*

@Serializable
sealed class SerializablePlayerInventory (
    private val itemList: Array<SerializableItemStack>,
    val armour: Array<SerializableArmourPiece>,
    val offHand: SerializableItemStack,
    val xp: Int,
    val playerId: String
) : SerializableInventory<PlayerInventory>(itemList.toList()) {

    abstract val player: PlayerEntity?

    override fun toInventory(): PlayerInventory {
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

            if (this.player.world.isClient) return SerializableClientPlayerInventory(items.toTypedArray(), armour.toTypedArray(), this.offHand[0].serializable(), this.player.totalExperience, this.player.uuidAsString)
            else return SerializableServerPlayerInventory(items.toTypedArray(), armour.toTypedArray(), this.offHand[0].serializable(), this.player.totalExperience, this.player.uuidAsString)
        }

        fun PlayerEntity.restoreInventory(inventory: SerializablePlayerInventory) = inventory.restoreInventory(this)

        private fun getKey(item: ItemStack) = Registry.ITEM.getId(item.item).toString()
    }

    @Serializable
    @Environment(EnvType.CLIENT)
    class SerializableClientPlayerInventory(
        private val itemArray: Array<SerializableItemStack>,
        val armourList: Array<SerializableArmourPiece>,
        val offHandStack: SerializableItemStack,
        val playerXp: Int,
        val uuid: String
    ) : SerializablePlayerInventory(itemArray, armourList, offHandStack, playerXp, uuid) {
        @Transient
        override val player = MinecraftClient.getInstance().player
    }

    @Environment(EnvType.SERVER)
    @Serializable
    class SerializableServerPlayerInventory(
        private val itemArray: Array<SerializableItemStack>,
        val armourList: Array<SerializableArmourPiece>,
        val offHandStack: SerializableItemStack,
        val playerXp: Int,
        val uuid: String
    ) : SerializablePlayerInventory(itemArray, armourList, offHandStack, playerXp, uuid) {
        override val player: ServerPlayerEntity?
            get() = server?.playerManager?.getPlayer(UUID.fromString(this.uuid))

        companion object {
            private var server: MinecraftServer? = null
            internal fun onStart(server: MinecraftServer) { Companion.server = server }
        }
    }
}