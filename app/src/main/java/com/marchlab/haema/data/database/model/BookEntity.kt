package com.marchlab.haema.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
    val author: String,
    val publisher: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String,
    val rating: Int,
    val review: String,
    val date: Long,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
/**
id	long
title	string
author	string
publisher	string
pubdate	string
image_url	string
rate	double
review	string
created_at	time
updated_at	time
*/