package com.marchlab.haema.ui.main.calendar

import com.marchlab.haema.domain.model.Emotion
import java.util.*

data class Day(
    var calendar: Calendar,
    var state: DayState,
    var isToday: Boolean,
    var isFuture: Boolean,
    var emotion: Emotion?
)

enum class DayState {
    PreviousMonth,
    ThisMonth,
    NextMonth
}