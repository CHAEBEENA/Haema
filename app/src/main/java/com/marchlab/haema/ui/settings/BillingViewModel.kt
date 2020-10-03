package com.marchlab.haema.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marchlab.haema.domain.usecase.setting.CheckIfPurchasedUseCase
import com.marchlab.haema.domain.usecase.setting.SetPurchasedUseCase
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.debug

class BillingViewModel(
    private val checkIfPurchasedUseCase: CheckIfPurchasedUseCase,
    private val setPurchasedUseCase: SetPurchasedUseCase
): ViewModel() {

    private val _isPurchased = MutableLiveData<Result<Boolean>>()
    val isPurchased: LiveData<Result<Boolean>>
        get() = _isPurchased

    fun setPurchased(purchased: Boolean) {
        Timber.debug { "BillingViewModel; setPurchased($purchased)" }
        viewModelScope.launch { setPurchasedUseCase(purchased, _isPurchased) }
    }
}
