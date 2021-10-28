package tech.shirok1.ncm.matcher.java.libncmapi

import tech.shirok1.ncm.matcher.java.libncmapi.Base64Util.base64Decode
import tech.shirok1.ncm.matcher.java.libncmapi.Base64Util.base64Encode
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object AESCrypt {

    private const val transformation = "AES/ECB/PKCS7Padding"

    fun encryptToBase64(inpute: String, passworld: String): String {
        return encryptToBase64(inpute, passworld.toByteArray(Charsets.UTF_8))
    }

    @Suppress("GetInstance")
    fun encryptToBase64(inpute: String, passworld: ByteArray): String {
        val encrypt = encrypt(passworld, inpute)
        return String(base64Encode(encrypt), Charsets.UTF_8)
    }

    private fun encrypt(passworld: ByteArray, inpute: String): ByteArray {
        val cipher = Cipher.getInstance(transformation).apply {
            init(Cipher.ENCRYPT_MODE, SecretKeySpec(passworld, "AES"))
        }
        val encrypt = cipher.doFinal(inpute.toByteArray())
        return encrypt
    }


    @Suppress("GetInstance")
    fun decrypt(inpute: String, passworld: ByteArray): String {
        val cipher = Cipher.getInstance(transformation).apply {
            init(Cipher.DECRYPT_MODE, SecretKeySpec(passworld, "AES"))
        }
        val encrypt = cipher.doFinal(base64Decode(inpute))
        return String(encrypt, Charsets.UTF_8)
    }

    fun decrypt(inpute: String, passworld: String): String {
        return decrypt(inpute, passworld.toByteArray(Charsets.UTF_8))
    }
}