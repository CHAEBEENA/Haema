package com.marchlab.haema.ui.main.billing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marchlab.haema.domain.usecase.setting.SetPurchasedUseCase
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.launch

class CheckBillingStateViewModel(
    private val setPurchasedUseCase: SetPurchasedUseCase
): ViewModel() {

    private val _isPurchased = MutableLiveData<Result<Boolean>>()
    val isPurchased: LiveData<Result<Boolean>>
        get() = _isPurchased

    fun setPurchased(purchased: Boolean) {
        viewModelScope.launch { setPurchasedUseCase(purchased, _isPurchased) }
    }
}