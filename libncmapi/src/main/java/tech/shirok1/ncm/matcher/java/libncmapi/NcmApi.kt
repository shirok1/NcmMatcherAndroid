package tech.shirok1.ncm.matcher.java.libncmapi

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.*
import okio.IOException
import tech.shirok1.ncm.matcher.java.libncmapi.model.*


object NcmApi {
    private val client: OkHttpClient by lazy { OkHttpClient.Builder().retryOnConnectionFailure(true).build() }
    private const val albumUrl = "https://music.163.com/api/album"
    private const val songUrl = "https://music.163.com/api/song/detail"
    private val jsonConvert = Json { ignoreUnknownKeys = true; coerceInputValues = true }

/*    suspend fun getAlbumMeta(id: Int): AlbumMeta? =
        tryGetFromApi(
            { it.url("$albumUrl/$id") },
            AlbumInfoResponse::album
        )*/

    fun getAlbumMetaAnd(id: Int, logger: ((String) -> Unit)?, callback: (AlbumMeta?) -> Unit) =
        tryGetFromApiAnd(
            {
                it.url("$albumUrl/$id")
                    .addHeader("X-Real-IP", "211.161.244.70")
                    .addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36")
                    .addHeader("Cookie", "NMTID=00OyRcZbiTvYCrO3kRVqWehKG2RI0EAAAF8hIsNIA")
            },
            AlbumInfoResponse::album, logger ?: {}, callback
        )

/*    fun getSongMeta(id: Long, logger: ((String) -> Unit)?): SongMeta? =
        tryGetFromApi<SongInfoResponse, SongMeta>(
            { it.url("$songUrl/[$id]") }, logger ?: {}
        ) { it.songs.firstOrNull() }*/

    fun getSongMetaAnd(id: Long, logger: ((String) -> Unit)?, callback: (SongMeta?) -> Unit) =
        tryGetFromApiAnd<SongInfoResponse, SongMeta>(
            { it.url("$songUrl/?ids=[$id]") },
            { it.songs.firstOrNull() }, logger ?: {}, callback
        )

/*    private inline fun <reified TResponse : IApiResponse, TReturn> tryGetFromApi(
        buildUrl: (Request.Builder) -> Request.Builder,
        crossinline logger: (String) -> Unit,
        extract: (TResponse) -> TReturn?,
    ): TReturn? {
        val request = buildUrl(Request.Builder()).get().build()
        return try {
            val responseJson = client.newCall(request).execute().body?.string()
            return resolveResponse<TResponse>(responseJson, logger, request.url.toString())?.let(extract)
        } catch (e: IOException) {
            logger(e.toString())
            return null
        }
    }*/

    private inline fun <reified TResponse : IApiResponse, TReturn> tryGetFromApiAnd(
        buildUrl: (Request.Builder) -> Request.Builder,
        crossinline extract: (TResponse) -> TReturn?,
        crossinline logger: (String) -> Unit,
        crossinline callback: (TReturn?) -> Unit,
    ): Call {
        val request = buildUrl(Request.Builder()).get().build()
        logger("Start $request.")
        client.newCall(request).apply {
            enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (call.isCanceled()) {
                        logger("call to ${call.request().url} canceled")
                    } else logger(e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    callback(resolveResponse<TResponse>(response.body?.string(),
                        logger, call.request().url.toString())?.let(extract))
                }
            })
            return this
        }
    }

    private inline fun <reified TResponse : IApiResponse> resolveResponse(
        responseJson: String?,
        logger: (String) -> Unit,
        url: String,
    ): TResponse? {
        if (!responseJson.isNullOrEmpty()) {
            val code =
                jsonConvert.parseToJsonElement(responseJson).jsonObject["code"]?.jsonPrimitive?.intOrNull
            if (code != null) {
                logger("$url responded with code $code.")
                if (code == 200) {
                    return jsonConvert.decodeFromString(responseJson)

                }
            } else {
                logger("$url response don't have a code, invalid.")
                logger(responseJson)
            }
        } else {
            logger("$url responded nothing.")
        }
        return null
    }
}