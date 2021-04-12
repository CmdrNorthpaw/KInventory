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

/**
 * A convenience object that adds a serializer for [PlayerInventory].
 * Uses surrogate serialization and is basically identical to [SerializablePlayerInventory]
 * You can either invoke it directly or use the [serializer] extension function for PlayerInventory
 * */
object PlayerInventorySerializer : KSerializer<PlayerInventory> {
    override fun deserialize(decoder: Decoder): PlayerInventory {
        return decoder.decodeSerializableValue(SerializablePlayerInventory.serializer()).toInventory()
    }

    override val descriptor: SerialDescriptor = SerializablePlayerInventory.serializer().descriptor

    override fun serialize(encoder: Encoder, value: PlayerInventory) {
        encoder.encodeSerializableValue(SerializablePlayerInventory.serializer(), value.serializable())
    }

    private fun getKey(stack: ItemStack) = Registry.ITEM.getKey(stack.item).toString()

    fun PlayerInventory.serializer(): KSerializer<PlayerInventory> = PlayerInventorySerializer
}