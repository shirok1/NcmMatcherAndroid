package tech.shirok1.ncm.matcher.java.libncmapi

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.math.BigInteger
import java.security.MessageDigest

object MD5Util {
    private const val bufferSize = 1024
    fun getFileMD5(file: File, radix: Int = 32): String {
        if (!file.isFile) throw FileNotFoundException()
        return try {
            val buffer = ByteArray(bufferSize)
            var len: Int
            val digest = MessageDigest.getInstance("MD5")
            val stream = FileInputStream(file)
            while (stream.read(buffer, 0, bufferSize).also { len = it } != -1) {
                digest.update(buffer, 0, len)
            }
            stream.close()
            BigInteger(1, digest.digest()).toString(radix)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}