package tech.shirok1.ncm.matcher.android

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.distinctUntilChanged
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import tech.shirok1.ncm.matcher.java.libncmapi.NcmUrlValidate

class MainViewModel : ViewModel() {
    val ncmUrl: MutableLiveData<String> = MutableLiveData("")
    val songId: LiveData<Long?> = distinctUntilChanged(map(ncmUrl) { NcmUrlValidate.getSongId(it) })
    val selectEnabled: LiveData<Boolean> = map(songId) { it != null }
    val audioFileUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val metaReady: MutableLiveData<Boolean> = MutableLiveData(false)
    val finishEnabled: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(audioFileUri) {
            value = it != null && selectEnabled.value == true && metaReady.value == true
        }
        addSource(selectEnabled) {
            value = audioFileUri.value != null && it && metaReady.value == true
        }
        addSource(metaReady) {
            value = audioFileUri.value != null && selectEnabled.value == true && it
        }
    }
}