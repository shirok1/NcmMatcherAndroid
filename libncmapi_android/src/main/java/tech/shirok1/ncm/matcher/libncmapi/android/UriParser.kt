package tech.shirok1.ncm.matcher.libncmapi.android

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.BaseColumns
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import kotlin.io.path.Path


object UriParser {
    @Suppress("DEPRECATION")
    @RequiresApi(Build.VERSION_CODES.O)
    fun Uri.getMediaUri(context: Context, mediaTypeUri: Uri): Uri {
        if ("com.android.externalstorage.documents" == this.authority) {
            val split = DocumentsContract.getDocumentId(this).split(":")
            if ("primary".equals(split[0], ignoreCase = true)) {
                val fullPath = split[1]
                val fileName = Path(fullPath).fileName.toString()
                val directory = fullPath.dropLast(fileName.length)
//                queryTest(context, mediaTypeUri)
                context.contentResolver.query(
                    mediaTypeUri,
                    arrayOf(
                        BaseColumns._ID,
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            MediaStore.MediaColumns.RELATIVE_PATH
                        } else {
                            MediaStore.MediaColumns.DATA
                        },
                        MediaStore.MediaColumns.DISPLAY_NAME,
                    ),
                    "${
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            MediaStore.MediaColumns.RELATIVE_PATH
                        } else {
                            MediaStore.MediaColumns.DATA
                        }
                    } == ? AND ${MediaStore.MediaColumns.DISPLAY_NAME} == ?",
                    arrayOf(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        directory
                    } else {
                        Environment.getExternalStorageDirectory().toString() + "/" + fullPath
                    }, fileName), "${BaseColumns._ID} ASC"
                )?.use {
                    val idColumn = it.getColumnIndexOrThrow(BaseColumns._ID)
                    if (it.moveToNext()) {
                        return ContentUris.withAppendedId(mediaTypeUri, it.getLong(idColumn))
                    }
                }
            }
        }
        Log.e("UriParse", "Failed to parse $this to media uri.")
        return this
    }

    @Suppress("DEPRECATION")
    private fun queryTest(context: Context, mediaTypeUri: Uri) {
        context.contentResolver.query(mediaTypeUri,
            arrayOf(BaseColumns._ID, MediaStore.MediaColumns.DATA),
            "",
            arrayOf(),
            "${BaseColumns._ID} ASC")?.use {
            val id = it.getColumnIndexOrThrow(BaseColumns._ID)
            val data = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            while (it.moveToNext()) {
                Log.i("QueryTest",
                    "${it.getLong(id)} ${it.getString(data)}")
            }
        }
    }
}