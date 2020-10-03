package com.marchlab.haema.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDate

@Parcelize
data class Book(
    val id: Long,
    val title: String,
    val author: String,
    val publisher: String,
    val imageUrl: String,
    val rating: Int,
    val review: String,
    val date: LocalDate,
    val createdAt: Long
): Parcelable