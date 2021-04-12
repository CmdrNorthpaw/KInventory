package uk.cmdrnorthpaw.kinventory.inventory

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.client.network.ClientPlayerEntity
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

/**
 * Represents an inventory that belongs to a [PlayerEntity]
 * Due to the way Minecraft's client-server model works, this is a sealed class, with implementations that should be used
 * depending on whether your code is running on the logical client or the logical server.
 * [SerializableServerPlayerInventory] should be used when running on the logical server (usually in single player or a dedicated server)
 * [SerializableClientPlayerInventory] should be used on the logical client (mods that don't need to be installed on a server to work in multiplayer)
 * The only difference between the two is how the [player] field is obtained
 * */
@Serializable
sealed class SerializablePlayerInventory (
    private val itemList: List<SerializableItemStack>,
    val armour: List<SerializableArmourPiece>,
    val offHand: SerializableItemStack,
    val xp: Int,
    val playerId: String
) : SerializableInventory<PlayerInventory>(itemList.toList()) {

    /**
     * The [PlayerEntity] this inventory belongs to
     * Implementation differs depending on whether you are using [SerializableClientPlayerInventory] or [SerializableServerPlayerInventory]
     * */
    abstract val player: PlayerEntity?

    /**
     * This class's implementation of [SerializableInventory]'s [toInventory] function.
     * Converts a [SerializablePlayerInventory] to a [PlayerInventory]
     * @return A [PlayerInventory] built from this class.
     * */
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
        /**
         * Converts a [PlayerInventory] to a [SerializablePlayerInventory]
         * This function can distinguish if the player is on the client or the server
         * and will return a [SerializableClientPlayerInventory] or [SerializableServerPlayerInventory] as appropriate
         * @return A [SerializablePlayerInventory] from a [PlayerInventory]
         * */
        fun PlayerInventory.serializable(): SerializablePlayerInventory {
            val items = mutableListOf<SerializableItemStack>()
            val armour = mutableListOf<SerializableArmourPiece>()

            this.main.forEach { items.add(SerializableItemStack(getKey(it), it.count, it.tag.toString())) }
            this.armor.forEach {
                val armourPiece: ArmorItem
                if (it.item !is ArmorItem) return@forEach else armourPiece = it.item as ArmorItem
                armour.add(SerializableArmourPiece(getKey(it), it.tag.toString(), armourPiece.slotType))
            }

            return if (this.player.world.isClient) SerializableClientPlayerInventory(items, armour, this.offHand[0].serializable(), this.player.totalExperience, this.player.uuidAsString)
            else SerializableServerPlayerInventory(items, armour, this.offHand[0].serializable(), this.player.totalExperience, this.player.uuidAsString)
        }

        fun PlayerEntity.restoreInventory(inventory: SerializablePlayerInventory) = inventory.restoreInventory(this)

        private fun getKey(item: ItemStack) = Registry.ITEM.getId(item.item).toString()
    }

    /**
     * Represents a [SerializablePlayerInventory] running on the client
     * @property player This class's implementation of [player]
     * */
    @Serializable
    @Environment(EnvType.CLIENT)
    class SerializableClientPlayerInventory(
        private val itemArray: List<SerializableItemStack>,
        val armourList: List<SerializableArmourPiece>,
        val offHandStack: SerializableItemStack,
        val playerXp: Int,
        val uuid: String
    ) : SerializablePlayerInventory(itemArray, armourList, offHandStack, playerXp, uuid) {
        /**
         * The [ClientPlayerEntity] who owns this inventory
         * Will be null if the player is not currently logged into a world
         * */
        override val player
            get() = MinecraftClient.getInstance().player
    }

    /**
     * An implementation of [SerializablePlayerInventory] to be used on a server
     * @property player This class's implementation of [player]
     * */
    @Environment(EnvType.SERVER)
    @Serializable
    class SerializableServerPlayerInventory(
        private val itemArray: List<SerializableItemStack>,
        val armourList: List<SerializableArmourPiece>,
        val offHandStack: SerializableItemStack,
        val playerXp: Int,
        val uuid: String
    ) : SerializablePlayerInventory(itemArray, armourList, offHandStack, playerXp, uuid) {
        override val player: ServerPlayerEntity?
            /**
             * The [ServerPlayerEntity] who owns this inventory
             * Will be null if the player is not currently logged in or if you are calling this method during server startup
             * Either way, you should not be using this field in either of those situations
             * */
            get() = server?.playerManager?.getPlayer(UUID.fromString(this.uuid))

        companion object {
            private var server: MinecraftServer? = null
            internal fun onStart(server: MinecraftServer) { Companion.server = server }
        }
    }
}