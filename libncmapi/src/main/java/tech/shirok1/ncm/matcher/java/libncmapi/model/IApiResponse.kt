package tech.shirok1.ncm.matcher.java.libncmapi.model

import kotlinx.serialization.Serializable

@Serializable
data class AlbumInfoResponse(
    val album: AlbumMeta, override val code: Int,
) : IApiResponse

@Serializable
data class SongInfoResponse(
    val songs: List<SongMeta>, override val code: Int,
) : IApiResponse

interface IApiResponse {
    val code: Int
}

@Serializable
data class AlbumMeta(
    val alias: List<String>,
    val artist: Artist,
    val artists: List<Artist>,
    val blurPicUrl: String,
    val briefDesc: String,
    val commentThreadId: String,
    val company: String,
    val companyId: Int,
    val copyrightId: Int,
    val description: String,
    val id: Long,
    val mark: Int,
    val name: String,
    val pic: Long,
    val picId: Long,
    val picUrl: String,
    val publishTime: Long,
    val size: Int,
    val songs: List<SongMeta>,
    val tags: String,
    val type: String,
)

@Serializable
data class Artist(
    val alias: List<String>,
    val briefDesc: String,
    val id: Long,
    val img1v1Id: Long,
    val img1v1Url: String,
    val name: String,
    val picId: Long,
    val picUrl: String,
    val trans: String,
)

@Serializable
data class SongMeta(
    val album: AlbumMeta,
    val alias: List<String>,
    val artists: List<Artist>,
    val disc: String,
    val duration: Long,
    val id: Long,
    val mvid: Long,
    val name: String,
    val no: Int,
    val position: Int,
    val score: Int,
    val status: Int,
)