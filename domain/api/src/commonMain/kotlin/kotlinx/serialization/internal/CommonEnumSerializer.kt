package kotlinx.serialization.internal

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Opeapi generator add this old dependency to all generated models
 * // TODO fix openapi generation to new serialization api and remove this class
 *
 *
 * Created by Martin Forejt on 25.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
open class CommonEnumSerializer<E : Enum<E>>(
    name: String,
    private val values: Array<E>,
    private val serialized: Array<String>
) : KSerializer<E> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(name, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: E) {
        encoder.encodeString(serialized[value.ordinal])
    }

    override fun deserialize(decoder: Decoder): E {
        decoder.decodeString().let { value ->
            val index = serialized.indexOfFirst { it == value }
            return if (index == -1) {
                values[0]
            } else {
                values[index]
            }
        }
    }

}