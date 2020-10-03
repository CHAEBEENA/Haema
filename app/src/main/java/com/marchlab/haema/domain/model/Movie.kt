package com.marchlab.haema.domain.model

import org.threeten.bp.LocalDate

data class Movie(
    val id: Long,
    val posterUrl: String,
    val title: String,
    val release: String,
    val directors: String,
    val actors: String,
    val date: LocalDate,
    val place: String,
    val people: String,
    val rate: Int,
    val review: String
)

/*
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