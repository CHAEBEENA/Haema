package com.marchlab.haema.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marchlab.haema.domain.usecase.base.invoke
import com.marchlab.haema.domain.usecase.setting.CheckIfPurchasedUseCase
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val checkIfPurchasedUseCase: CheckIfPurchasedUseCase
): ViewModel() {

    private val _isPurchased = MutableLiveData<Result<Boolean>>()
    val isPurchased: LiveData<Result<Boolean>>
        get() = _isPurchased

    init {
        viewModelScope.launch { checkIfPurchasedUseCase(_isPurchased) }
    }

}