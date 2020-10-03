package com.marchlab.haema.ui.applock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marchlab.haema.domain.usecase.setting.GetPasswordUseCase
import com.marchlab.haema.ui.applock.AppLockActivity.Companion.passwordCount
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.launch

class AppLockViewModel(
    private val getPasswordUseCase: GetPasswordUseCase
): ViewModel() {

    private val _input = MutableLiveData<List<String>>()
    val input: LiveData<List<String>>
        get() = _input

    private val _password = MutableLiveData<Result<String?>>()
    val password: LiveData<Result<String?>>
    get() = _password


    init {
        viewModelScope.launch {
            getPasswordUseCase(Unit, _password)
        }
    }

    fun appendInput(text: String){
        _input.value
            ?.toMutableList()
            ?.also { if(it.size + text.length > passwordCount) return else it.add(text)  }
            ?.let { _input.value = it }
            ?:_input.postValue(listOf(text))
    }

    fun removeInput(){
        _input.value
            ?.toMutableList()
            ?.also { if(it.size-1 >= 0 ) it.removeAt(it.size-1) else return }
            ?.let { _input.value = it }
            ?:_input.postValue(emptyList())
    }

    fun clearInput(){
        _input.postValue(emptyList())
    }

}