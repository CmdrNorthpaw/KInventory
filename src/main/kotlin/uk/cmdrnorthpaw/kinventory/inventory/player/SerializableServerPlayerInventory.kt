package uk.cmdrnorthpaw.kinventory.inventory.player

import kotlinx.serialization.Serializable
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.MinecraftServer
import net.minecraft.server.PlayerManager
import net.minecraft.server.dedicated.MinecraftDedicatedServer
import net.minecraft.server.network.ServerPlayerEntity
import uk.cmdrnorthpaw.kinventory.model.SerializableArmourPiece
import uk.cmdrnorthpaw.kinventory.model.SerializableItemStack
import java.util.*

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
        internal fun onStart(server: MinecraftServer) { this.server = server }
    }
}