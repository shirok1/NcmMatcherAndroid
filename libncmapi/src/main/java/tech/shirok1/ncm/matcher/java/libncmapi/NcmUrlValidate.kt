package tech.shirok1.ncm.matcher.java.libncmapi

object NcmUrlValidate {
    private val songIdRegex: Regex = """music\.163\.com/[#m]/song\?id=(\d+)""".toRegex()
    fun getSongId(url: String): Long? = songIdRegex.find(url)?.groups?.get(1)?.value?.toLong()
}