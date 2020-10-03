package com.marchlab.haema.ui.settings

import androidx.lifecycle.*
import com.marchlab.haema.domain.usecase.base.invoke
import com.marchlab.haema.domain.usecase.setting.CheckAppLockUseCase
import com.marchlab.haema.domain.usecase.setting.CheckIfPurchasedUseCase
import com.marchlab.haema.domain.usecase.setting.RemovePasswordUseCase
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.launch
import java.security.PrivateKey

class SettingsViewModel(
    private val checkIfPurchasedUseCase: CheckIfPurchasedUseCase,
    private val removePasswordUseCase: RemovePasswordUseCase,
    private val checkAppLockUseCase: CheckAppLockUseCase
): ViewModel() {

    private val _isPurchased = MutableLiveData<Result<Boolean>>()
    val isPurchased: LiveData<Result<Boolean>>
        get() = _isPurchased

    private val _isAppLock = MutableLiveData<Result<Boolean>>()
    val isAppLock: LiveData<Result<Boolean>>
        get() = _isAppLock

    init {
        viewModelScope.launch {
            checkIfPurchasedUseCase(_isPurchased)
            checkAppLockUseCase(_isAppLock)
        }
    }

    fun removePassword() {
        viewModelScope.launch {
            removePasswordUseCase()
        }
    }

    fun checkAppLock() {
        viewModelScope.launch {
            checkAppLockUseCase(_isAppLock)
        }
    }

    fun checkIfPurchased() {
        viewModelScope.launch { checkIfPurchasedUseCase(_isPurchased) }
    }
}