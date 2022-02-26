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
import uk.cmdrnorthpaw.kinventory.serializers.NotSerializable
import uk.cmdrnorthpaw.kinventory.serializers.PlayerInventorySerializer
import java.util.*

/**
 * Represents an inventory that belongs to a [PlayerEntity].
 * Due to the way Minecraft's client-server model works, this is a sealed class, with implementations that should be used
 * depending on whether your code is running on the logical client or the logical server.
 * [SerializableServerPlayerInventory] should be used when running on the logical server (usually in single player or a dedicated server).
 * [SerializableClientPlayerInventory] should be used on the logical client (mods that don't need to be installed on a server to work in multiplayer).
 * The only difference between the two is how the [player] field is obtained
 *
 * @param itemList The items in this inventory. Includes the hotbar, but not the armour or off-hand.
 * @param armour Any armour that this player is wearing.
 * @param offHand The [ItemStack] in the player's off hand
 * @param playerId The UUID of the inventory's owner as a [String]
 * */
@Serializable
sealed class SerializablePlayerInventory (
    internal val itemList: List<SerializableItemStack>,
    val armour: List<SerializableArmourPiece>,
    val offHand: SerializableItemStack,
    val playerId: String
) : SerializableInventory<@Serializable(PlayerInventorySerializer::class) PlayerInventory>(itemList.toList()) {

    /**
     * The [PlayerEntity] this inventory belongs to.
     * Implementation differs depending on whether you are using [SerializableClientPlayerInventory] or [SerializableServerPlayerInventory].
     * */
    abstract val player: PlayerEntity?

    /**
     * This class's implementation of [SerializableInventory]'s [toInventory] function.
     * Converts a [SerializablePlayerInventory] to a [PlayerInventory].
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

    companion object : SerializableInventoryCompanion<PlayerInventory, SerializablePlayerInventory> {
        /**
         * Converts a [PlayerInventory] to a [SerializablePlayerInventory].
         * This function can distinguish if the player is on the client or the server
         * and will return a [SerializableClientPlayerInventory] or [SerializableServerPlayerInventory] as appropriate.
         * @return A [SerializablePlayerInventory] from a [PlayerInventory]
         * */
        fun PlayerInventory.serializable(): SerializablePlayerInventory = getSerializable(this)

        private fun getKey(item: ItemStack) = Registry.ITEM.getId(item.item).toString()

        override fun getSerializable(from: PlayerInventory): SerializablePlayerInventory {
            val items = mutableListOf<SerializableItemStack>()
            val armour = mutableListOf<SerializableArmourPiece>()

            from.main.forEach { items.add(SerializableItemStack(getKey(it), it.count, it.tag.toString())) }
            from.armor.forEach {
                val armourPiece: ArmorItem
                if (it.item !is ArmorItem) return@forEach else armourPiece = it.item as ArmorItem
                armour.add(SerializableArmourPiece(getKey(it), it.tag.toString(), armourPiece.slotType))
            }

            return if (from.player.world.isClient) SerializableClientPlayerInventory(items, armour, from.offHand[0].serializable(), from.player.uuidAsString)
            else SerializableServerPlayerInventory(items, armour, from.offHand[0].serializable(), from.player.uuidAsString)


        }
    }

    /**
     * Represents a [SerializablePlayerInventory] running on the client.
     * @property player This class's implementation of [player]
     * */
    @Serializable
    @Environment(EnvType.CLIENT)
    class SerializableClientPlayerInventory (
        internal val itemArray: List<SerializableItemStack>,
        internal val armourList: List<SerializableArmourPiece>,
        internal val offHandStack: SerializableItemStack,
        internal val uuid: String
    ) : SerializablePlayerInventory(itemArray, armourList, offHandStack, uuid) {
        /**
         * The [ClientPlayerEntity] who owns this inventory
         * Will be null if the player is not currently logged into a world
         * */
        override val player
            get() = MinecraftClient.getInstance().player
    }

    /**
     * An implementation of [SerializablePlayerInventory] to be used on a server.
     * @property player This class's implementation of [player]
     * */
    @Environment(EnvType.SERVER)
    @Serializable
    class SerializableServerPlayerInventory(
        internal val itemArray: List<SerializableItemStack>,
        internal val armourList: List<SerializableArmourPiece>,
        internal val offHandStack: SerializableItemStack,
        internal val uuid: String
    ) : SerializablePlayerInventory(itemArray, armourList, offHandStack, uuid) {
        override val player: ServerPlayerEntity?
            /**
             * The [ServerPlayerEntity] who owns this inventory
             * Will be null if the player is not currently logged in or if you are calling this method during server startup
             * Either way, you should not be using this field in either of those situations
             * */
            get() = server?.playerManager?.getPlayer(UUID.fromString(this.uuid))

        companion object {
            internal var server: MinecraftServer? = null
            internal fun onStart(server: MinecraftServer) { Companion.server = server }
        }
    }
}