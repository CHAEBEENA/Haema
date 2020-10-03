package com.marchlab.haema.util.extensions

import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate

fun LocalDate.toFormatString() = "${year}년 ${monthValue}월 ${dayOfMonth}일 ${dayOfWeek.toFormatString()}"

fun DayOfWeek.toFormatString() = when(this) {
    DayOfWeek.SUNDAY -> "일요일"
    DayOfWeek.MONDAY -> "월요일"
    DayOfWeek.TUESDAY -> "화요일"
    DayOfWeek.WEDNESDAY -> "수요일"
    DayOfWeek.THURSDAY -> "목요일"
    DayOfWeek.FRIDAY -> "금요일"
    DayOfWeek.SATURDAY -> "토요일"
}