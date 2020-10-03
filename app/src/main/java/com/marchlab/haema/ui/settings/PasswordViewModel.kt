package com.marchlab.haema.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marchlab.haema.domain.usecase.setting.SetPasswordUseCase
import com.marchlab.haema.ui.applock.AppLockActivity
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.launch

class PasswordViewModel(
    private val setPasswordUseCase: SetPasswordUseCase
) : ViewModel() {

    private val _setPasswordResult = MutableLiveData<Result<Unit>>()
    val setPasswordResult: LiveData<Result<Unit>>
        get() = _setPasswordResult


    private val _input = MutableLiveData<List<String>>()
    val input: LiveData<List<String>>
        get() = _input

    fun appendInput(text: String){
        _input.value
            ?.toMutableList()
            ?.also { if(it.size + text.length > AppLockActivity.passwordCount) return else it.add(text)  }
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

    fun setPassword(password: String) {
        viewModelScope.launch { setPasswordUseCase(password, _setPasswordResult) }
    }
}
