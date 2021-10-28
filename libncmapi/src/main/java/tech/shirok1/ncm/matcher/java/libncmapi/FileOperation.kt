package tech.shirok1.ncm.matcher.java.libncmapi

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.SupportedFileFormat
import org.jaudiotagger.audio.exceptions.UnableToModifyFileException
import org.jaudiotagger.tag.FieldKey
import tech.shirok1.ncm.matcher.java.libncmapi.CommentMaker.toComment
import tech.shirok1.ncm.matcher.java.libncmapi.model.KeyMeta
import tech.shirok1.ncm.matcher.java.libncmapi.model.SongMeta
import java.io.File

object FileOperation {
    fun File.writeMeta(songMeta: SongMeta) {
        val audioFile = AudioFileIO.read(this)
        val format =
            when (SupportedFileFormat.valueOf(audioFile.audioHeader.format.uppercase())) {
                SupportedFileFormat.MP3 -> "mp3"
                SupportedFileFormat.FLAC -> "flac"
                else -> throw UnableToModifyFileException("File is not either mp3 or flac!")
            }
        audioFile.tagOrCreateAndSetDefault.apply {
            setField(FieldKey.TITLE, songMeta.name)
            setField(FieldKey.ALBUM, songMeta.album.name)
            setField(FieldKey.PERFORMER,
                songMeta.artists.joinToString(separator = "/") { it.name })
            setField(FieldKey.SINGLE_DISC_TRACK_NO, songMeta.no.toString())
            setField(FieldKey.COMMENT, KeyMeta(
                album = songMeta.album.name,
                albumId = songMeta.album.id,
                albumPic = songMeta.album.picUrl,
                albumPicDocId = songMeta.album.pic, // TODO
                alias = songMeta.alias.toTypedArray(),
                artist = songMeta.artists.toTypedArray(),
                musicId = songMeta.id,
                musicName = songMeta.name,
                mvId = songMeta.mvid,
                transNames = emptyArray(), // TODO ???
                format = format,
                bitrate = audioFile.audioHeader.bitRateAsNumber * 1000,
                duration = (audioFile.audioHeader.preciseTrackLength * 1000).toLong(),
                mp3DocId = MD5Util.getFileMD5(this@writeMeta)
            ).toComment())
        }
        audioFile.commit()
    }
}