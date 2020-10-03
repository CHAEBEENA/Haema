package com.marchlab.haema.ui.main.journal.date

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marchlab.haema.domain.usecase.base.invoke
import com.marchlab.haema.domain.usecase.journal.LoadAllDatesUseCase
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

class JournalDatePickerViewModel(
    private val loadAllDatesUseCase: LoadAllDatesUseCase
): ViewModel() {

    private val _unavailableDates = MutableLiveData<Result<List<LocalDate>>>()
    val unavailableDates: LiveData<Result<List<LocalDate>>>
        get() = _unavailableDates

    init {
        viewModelScope.launch { loadAllDatesUseCase(_unavailableDates) }
    }
}