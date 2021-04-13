package uk.cmdrnorthpaw.kinventory.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry
import uk.cmdrnorthpaw.kinventory.inventory.SerializablePlayerInventory
import uk.cmdrnorthpaw.kinventory.inventory.SerializablePlayerInventory.Companion.serializable
import uk.cmdrnorthpaw.kinventory.model.InventorySurrogateSerializer

/**
 * A convenience object that adds a serializer for [PlayerInventory].
 * Uses surrogate serialization and is basically identical to [SerializablePlayerInventory]
 * You can either invoke it directly or use the [serializer] extension function for PlayerInventory
 * */
object PlayerInventorySerializer : InventorySurrogateSerializer<PlayerInventory, SerializablePlayerInventory>(
    SerializablePlayerInventory.serializer(), SerializablePlayerInventory.Companion
) {
    fun PlayerInventory.serializer() = this@PlayerInventorySerializer
}