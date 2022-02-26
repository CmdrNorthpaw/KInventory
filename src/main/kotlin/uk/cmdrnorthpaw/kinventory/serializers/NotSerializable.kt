package uk.cmdrnorthpaw.kinventory.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A dummy serializer for any type arguments that the compiler wants to be [Serializable] but will never actually be serialized.
 * To the compiler it looks like a regular [KSerializer],
 * but if anyone actually tries to use it, it throws an error.
 *
 * @throws SerializationException
 * */

object NotSerializable : KSerializer<Any> {
    private val exception = SerializationException("""
        Classes serialized with NotSerializable should never actually be serialized
        but the compiler doesn't know that and will throw an error without it being marked as serialized
    """.trimIndent())

    override fun deserialize(decoder: Decoder): Any = throw exception

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Do not ever serialize this class")

    override fun serialize(encoder: Encoder, value: Any) = throw exception
}