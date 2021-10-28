package tech.shirok1.ncm.matcher.java.libncmapi.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.LongAsStringSerializer

@Serializable
data class KeyMeta(
    var album: String,
    var albumId: Long,
    var albumPic: String,
    @Serializable(with = LongAsStringSerializer::class)
    var albumPicDocId: Long,
    var alias: Array<String>,
    var artist: Array<@Serializable(with = ArtistKeyMetaSerializer::class) Artist>,
    var bitrate: Long,
    var duration: Long,
    var format: String,
    var mp3DocId: String,
    var musicId: Long,
    var musicName: String,
    var mvId: Long,
    var transNames: Array<String>,
)