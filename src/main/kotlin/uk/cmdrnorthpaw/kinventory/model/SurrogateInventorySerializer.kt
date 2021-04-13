package uk.cmdrnorthpaw.kinventory.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.inventory.Inventory

/**
 * This class is able to generate a [KSerializer] for an [Inventory] using a surrogate [SerializableInventory].
 * While you don't have to, I recommend extending it with an object, so that you can call it more easily.
 * The class is left open for that purpose.
 *
 *  @param I The [Inventory] which this class provides a serializer for.
 *  @param S The [SerializableInventory] representation of [I] that this class surrogates.
 *
 *  @param surrogate The [KSerializer] of [S]. There's no good way to get this from a type parameter,
 *  but the user can just call S.serializable() to obtain it.
 *  @param surrogateCompanion A [SerializableInventory.SerializableInventoryCompanion] which can convert [I] to its [S] representation
 * */
open class SurrogateInventorySerializer<I: Inventory, S: SerializableInventory<I>> (
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