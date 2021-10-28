package tech.shirok1.ncm.matcher.android

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import okhttp3.*
import tech.shirok1.ncm.matcher.java.libncmapi.NcmApi
import tech.shirok1.ncm.matcher.java.libncmapi.model.SongMeta
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class SingleInfoViewModel : ViewModel() {
    val meta: MutableLiveData<SongMeta?> = MutableLiveData<SongMeta?>(null)
    val isMetaAvailable: LiveData<Boolean> = map(meta) { it != null }
    val songName: LiveData<String?> = map(meta) { it?.name }
    val albumName: LiveData<String?> = map(meta) { it?.album?.name }
    val artistName: LiveData<String?> =
        map(meta) { it?.artists?.joinToString { it.name } }

    private var lastCall: Call? = null
    private val lastCallLock = ReentrantLock();
    fun loadMeta(id: Long) {
        meta.value = null
        lastCallLock.withLock {
            lastCall?.cancel()
            lastCall = NcmApi.getSongMetaAnd(id, { Log.i("vmLoadMeta", it) }) { meta.postValue(it) }
        }
    }

    private val loadImageClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cache(Cache(File.createTempFile("ImgCache", null).also { it.deleteOnExit() },
                128L * 1024 * 1024)).build()
    }
    private val loadImageCacheControl: CacheControl by lazy {
        CacheControl.Builder().maxAge(1, TimeUnit.DAYS).build()
    }
    val image: MutableLiveData<Bitmap?> = MutableLiveData<Bitmap?>(null)
    private var lastImageCall: Call? = null
    private val lastImageCallLock = ReentrantLock();
    private fun loadImage(url: String) {
        lastImageCallLock.withLock {
            lastImageCall?.cancel()
            loadImageClient.newCall(Request.Builder().url(url).cacheControl(loadImageCacheControl)
                .get().build()).apply {
                lastImageCall = this
                val thisImageCall = this
                enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        TODO("Not yet implemented")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (thisImageCall != lastImageCall) return
                        response.body?.byteStream()?.let {
                            image.postValue(BitmapFactory.decodeStream(it))
                            it.close()
                        }
                    }
                })
            }
        }
    }

    init {
        meta.observeForever {
            image.value = null
            it?.album?.picUrl?.let { url -> loadImage(url) }
        }
    }
}