package tech.shirok1.ncm.matcher.java.libncmapi.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.serializer
import kotlin.UnsupportedOperationException

class ArtistKeyMetaSerializer :
    KSerializer<Artist> {
    override fun serialize(encoder: Encoder, value: Artist) {
        encoder.encodeSerializableValue(serializer(), buildJsonArray {
            add(value.name)
            add(value.id)
        })
    }

    override fun deserialize(decoder: Decoder): Artist {
        throw UnsupportedOperationException("Can't deserialize")
    }

    override val descriptor = PrimitiveSerialDescriptor(
        "tech.shirok1.ncm.matcher.java.libncmapi.model.ArtistKeyMetaSerializer",
        PrimitiveKind.STRING)
}