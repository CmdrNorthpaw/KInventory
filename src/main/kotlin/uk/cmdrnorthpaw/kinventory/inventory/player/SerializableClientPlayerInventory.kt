package uk.cmdrnorthpaw.kinventory.inventory.player

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import uk.cmdrnorthpaw.kinventory.model.SerializableArmourPiece
import uk.cmdrnorthpaw.kinventory.model.SerializableItemStack

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