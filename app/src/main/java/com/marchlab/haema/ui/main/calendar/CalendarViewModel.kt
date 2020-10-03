package com.marchlab.haema.ui.main.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marchlab.haema.domain.usecase.base.invoke
import com.marchlab.haema.domain.usecase.setting.CheckIfPurchasedUseCase
import com.marchlab.haema.util.result.Result
import kotlinx.coroutines.launch
import java.util.*

class CalendarViewModel(
    private val checkIfPurchasedUseCase: CheckIfPurchasedUseCase
) : ViewModel() {

    private val _isPurchased = MutableLiveData<Result<Boolean>>()
    val isPurchased: LiveData<Result<Boolean>>
        get() = _isPurchased

    private val _date = MutableLiveData<Calendar>()
    val date: LiveData<Calendar>
        get() = _date

    init {
        viewModelScope.launch { checkIfPurchasedUseCase(_isPurchased) }
        _date.postValue(Calendar.getInstance())
    }

    fun setDate(calendar: Calendar) {
        _date.postValue(calendar)
    }

}
