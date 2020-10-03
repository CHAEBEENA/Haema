package com.marchlab.haema.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marchlab.haema.domain.model.Journal
import com.marchlab.haema.domain.usecase.base.invoke
import com.marchlab.haema.domain.usecase.journal.LoadJournalsUseCase
import com.marchlab.haema.domain.usecase.setting.CheckIfPurchasedUseCase
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class MainViewModel(
    private val checkIfPurchasedUseCase: CheckIfPurchasedUseCase,
    private val loadJournalsUseCase: LoadJournalsUseCase
): ViewModel() {

    private val _journals = MutableLiveData<Result<List<Journal>>>()
    val journals: LiveData<Result<List<Journal>>>
        get() = _journals

    private val _isPurchased = MutableLiveData<Result<Boolean>>()
    val isPurchased: LiveData<Result<Boolean>>
        get() = _isPurchased

    init {
        viewModelScope.launch { loadJournalsUseCase(_journals) }
    }

    fun checkIfPurchased() {
        viewModelScope.launch { checkIfPurchasedUseCase(_isPurchased) }
    }
}