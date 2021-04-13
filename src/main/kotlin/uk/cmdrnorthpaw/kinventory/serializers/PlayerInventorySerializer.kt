package uk.cmdrnorthpaw.kinventory.serializers

import net.minecraft.entity.player.PlayerInventory
import uk.cmdrnorthpaw.kinventory.inventory.SerializablePlayerInventory
import uk.cmdrnorthpaw.kinventory.model.SurrogateInventorySerializer

/**
 * A convenience object that adds a serializer for [PlayerInventory].
 * Uses surrogate serialization and is basically identical to [SerializablePlayerInventory]
 * You can either invoke it directly or use the [serializer] extension function for PlayerInventory
 * */
object PlayerInventorySerializer : SurrogateInventorySerializer<PlayerInventory, SerializablePlayerInventory>(
    SerializablePlayerInventory.serializer(), SerializablePlayerInventory.Companion
) {
    fun PlayerInventory.serializer() = this@PlayerInventorySerializer
}