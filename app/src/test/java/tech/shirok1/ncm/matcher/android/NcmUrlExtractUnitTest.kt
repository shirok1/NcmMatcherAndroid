package tech.shirok1.ncm.matcher.android

import org.junit.Assert
import org.junit.Test
import tech.shirok1.ncm.matcher.java.libncmapi.NcmUrlValidate

class NcmUrlExtractUnitTest {
    @Test
    fun getSongId_onValid() {
        Assert.assertEquals(4888581L,
            NcmUrlValidate.getSongId("https://music.163.com/#/song?id=4888581"))
        Assert.assertEquals(403180L,
            NcmUrlValidate.getSongId("http://music.163.com/#/song?id=403180"))
        Assert.assertEquals(558560729L,
            NcmUrlValidate.getSongId("https://y.music.163.com/m/song?id=558560729&userid=289356976"))
    }

    @Test
    fun getSongId_onInvalid() {
        Assert.assertEquals(null,
            NcmUrlValidate.getSongId("https://www.baidu.com/s?wd=%E7%BD%91%E6%98%93%E4%BA%91%E9%9F%B3%E4%B9%90&rsv_spt=1&rsv_iqid=0x91b1e95e00052b81&issp=1&f=8&rsv_bp=1&rsv_idx=2&ie=utf-8&rqlang=&tn=baiduhome_pg&ch=&rsv_enter=1&rsv_btype=i&rsv_dl=ib&inputT=6424"))
        Assert.assertEquals(null,
            NcmUrlValidate.getSongId("https://music.163.com/#/discover/toplist"))
    }
}