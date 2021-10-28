package tech.shirok1.ncm.matcher.java.libncmapi

import java.util.*

object Base64Util {
    private val encoder = Base64.getEncoder()

    fun base64EncodeToString(bytes: ByteArray): String {
        return encoder.encodeToString(bytes)
    }

    fun base64Encode(bytes: ByteArray): ByteArray {
        return encoder.encode(bytes)
    }

    private val decoder = Base64.getDecoder()

    fun base64Decode(str: String): ByteArray {
        return decoder.decode(str)
    }
}