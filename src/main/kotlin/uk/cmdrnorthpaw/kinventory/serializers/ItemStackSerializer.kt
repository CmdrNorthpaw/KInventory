package uk.cmdrnorthpaw.kinventory.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.item.ItemStack
import uk.cmdrnorthpaw.kinventory.model.SerializableItemStack
import uk.cmdrnorthpaw.kinventory.model.SerializableItemStack.Companion.serializable

object ItemStackSerializer : KSerializer<ItemStack> {
    override fun deserialize(decoder: Decoder): ItemStack {
        return decoder.decodeSerializableValue(SerializableItemStack.serializer()).toItemStack()
    }

    override val descriptor: SerialDescriptor = SerializableItemStack.serializer().descriptor

    override fun serialize(encoder: Encoder, value: ItemStack) {
        encoder.encodeSerializableValue(SerializableItemStack.serializer(), value.serializable())
    }
}