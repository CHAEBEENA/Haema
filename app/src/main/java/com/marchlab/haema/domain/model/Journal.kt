package com.marchlab.haema.domain.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDate

@Parcelize
data class Journal(
    val id: Long,
    val emotion: Emotion,
    val imageUri: Uri?,
    val content: String,
    val date: LocalDate,
    val createdAt: Long
): Parcelable