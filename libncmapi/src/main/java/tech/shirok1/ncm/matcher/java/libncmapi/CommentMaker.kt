package tech.shirok1.ncm.matcher.java.libncmapi

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tech.shirok1.ncm.matcher.java.libncmapi.NonAsciiEscaper.nonAsciiEscape
import tech.shirok1.ncm.matcher.java.libncmapi.model.KeyMeta

object CommentMaker {
    private val AESKey: ByteArray =
        byteArrayOf(0x23,
            0x31,
            0x34,
            0x6C,
            0x6A,
            0x6B,
            0x5F,
            0x21,
            0x5C,
            0x5D,
            0x26,
            0x30,
            0x55,
            0x3C,
            0x27,
            0x28)

    private val jsonSerializer = Json

    fun KeyMeta.toComment(): String {
        val metaString =
            "music:" + jsonSerializer.encodeToString(this).nonAsciiEscape()
        return "163 key(Don't modify):" + AESCrypt.encryptToBase64(metaString, AESKey)
    }
}