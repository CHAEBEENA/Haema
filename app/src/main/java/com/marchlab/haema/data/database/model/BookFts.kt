package com.marchlab.haema.data.database.model

import androidx.room.Entity
import androidx.room.Fts4
import com.marchlab.haema.data.database.model.BookEntity

@Fts4(contentEntity = BookEntity::class)
@Entity(tableName = "book_fts")
data class BookFts(
    val title: String,
    val author: String,
    val publisher: String,
    val review: String
)