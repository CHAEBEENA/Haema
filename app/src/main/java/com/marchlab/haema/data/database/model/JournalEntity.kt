package com.marchlab.haema.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "journal", indices = [Index(value = ["date"], unique = true)])
data class JournalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val emotion: Int,
    val date: Long,
    @ColumnInfo(name = "image_uri")
    val imageUri: String?,
    val content: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)