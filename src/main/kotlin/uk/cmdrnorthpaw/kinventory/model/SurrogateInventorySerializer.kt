package uk.cmdrnorthpaw.kinventory.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.inventory.Inventory

abstract class SurrogateInventorySerializer<I: Inventory, S: SerializableInventory<I>> (
    val surrogate: KSerializer<S>,
    private val surrogateCompanion: SerializableInventory.SerializableInventoryCompanion<I, S>
) : KSerializer<I> {
    override val descriptor: SerialDescriptor = surrogate.descriptor

    override fun deserialize(decoder: Decoder): I {
        return decoder.decodeSerializableValue(surrogate).toInventory()
    }

    override fun serialize(encoder: Encoder, value: I) {
        encoder.encodeSerializableValue(surrogate, surrogateCompanion.getSerializable(value))
    }
}