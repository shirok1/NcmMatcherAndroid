package tech.shirok1.ncm.matcher.android

import org.junit.Test

class MainViewModelUnitTest {
    /*@Test fun observableExpressionFieldTest() {
        val testInstance = MainActivityViewModel()
        println("url=${testInstance.ncmUrl.get()}, id=${testInstance.songId.get()}, enabled=${testInstance.selectEnabled.get()}")
        testInstance.songId.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                println("id changed to ${testInstance.songId.get()}")
            }
        })
        testInstance.selectEnabled.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                println("enabled changed to ${testInstance.selectEnabled.get()}")
            }
        })
        testInstance.ncmUrl.set("https://music.163.com/#/song?id=403180")
        println("url=${testInstance.ncmUrl.get()}, id=${testInstance.songId.get()}, enabled=${testInstance.selectEnabled.get()}")
    }*/
    @Test
    fun liveDataTest() {
        val testInstance = MainViewModel()
        println("url=${testInstance.ncmUrl.value}, id=${testInstance.songId.value}, enabled=${testInstance.selectEnabled.value}")
        testInstance.songId.observeForever {
            println("id changed to $it")
        }
        testInstance.selectEnabled.observeForever{
            println("enabled changed to $it")
        }
        testInstance.ncmUrl.value="https://music.163.com/#/song?id=403180"
        println("url=${testInstance.ncmUrl.value}, id=${testInstance.songId.value}, enabled=${testInstance.selectEnabled.value}")
    }
}