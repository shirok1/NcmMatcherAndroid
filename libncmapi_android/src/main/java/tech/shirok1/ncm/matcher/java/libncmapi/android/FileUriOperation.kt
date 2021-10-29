package tech.shirok1.ncm.matcher.java.libncmapi.android

import android.content.Context
import android.net.Uri
import android.util.Log
import org.jaudiotagger.audio.exceptions.UnableToModifyFileException
import org.jaudiotagger.tag.TagOptionSingleton
import tech.shirok1.ncm.matcher.java.libncmapi.FileOperation.writeMeta
import tech.shirok1.ncm.matcher.java.libncmapi.model.SongMeta
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

object FileUriOperation {
    private val makeSureIsAndroidSet: Unit by lazy {
        TagOptionSingleton.getInstance().isAndroid = true
    }

    fun Uri.writeMeta(context: Context, songMeta: SongMeta) {
        makeSureIsAndroidSet
        try {
            val cache =
                File.createTempFile("AudioTagCache", if (this.toString().endsWith(".mp3", true)) {
                    ".mp3"
                } else if (this.toString().endsWith(".flac", true)) {
                    ".flac"
                } else {
                    throw UnableToModifyFileException("File is not either mp3 or flac!")
                }, context.cacheDir)

            val cacheWriteStream = cache.outputStream()
            (context.contentResolver.openInputStream(this)
                ?: throw FileNotFoundException(path)).apply {
                copyTo(cacheWriteStream)
                close()
            }
            cacheWriteStream.close()

            cache.writeMeta(songMeta)

            val cacheReadStream = cache.inputStream()
            (context.contentResolver.openOutputStream(this)
                ?: throw FileNotFoundException(path)).apply {
                cacheReadStream.copyTo(this)
                close()
            }
            cacheReadStream.close()

            cache.delete()
            Log.i("FileOperation", "All complete, cache deleted.")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}