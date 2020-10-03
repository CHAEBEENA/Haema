package com.marchlab.haema.ui.main.journal.emotion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marchlab.haema.domain.model.Emotion

class EmotionViewModel : ViewModel() {

    private val _emotion = MutableLiveData<Emotion>()
    val emotion: LiveData<Emotion>
        get() = _emotion

    fun onChange(emotion: Emotion) = _emotion.postValue(emotion)

}
