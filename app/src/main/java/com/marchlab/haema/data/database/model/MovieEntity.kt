package com.marchlab.haema.data.database.model

import androidx.room.*

@Entity(tableName = "movie")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "poster_url")
    val posterUrl: String,
    val title: String,
    val release: String,
    val directors: String,
    val actors: String,
    val date: Long,
    val place: String,
    val people: String,
    val rate: Int,
    val review: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)

/**
id	long
title	string
directors	list<string>
actors	list<string>
rate	double
review	string
people	list<string>
time	string
location	string
poster_url	string
created_at	time
updated_at	time
 */