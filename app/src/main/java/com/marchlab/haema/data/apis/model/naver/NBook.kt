package com.marchlab.haema.data.apis.model.naver

import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

data class NBook(
    val title: String,
    val image: String,
    val author: String,
    val publisher: String,
    val pubdate: Int
)