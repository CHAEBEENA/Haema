package com.marchlab.haema.ui.imagepicker

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImagePickerViewModel : ViewModel() {

    private val _imageUris = MutableLiveData<List<Uri>>()
    val imageUris: LiveData<List<Uri>>
        get() = _imageUris

    fun onUriLoaded(uris: List<Uri>) = _imageUris.postValue(uris)
}
