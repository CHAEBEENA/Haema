package com.marchlab.haema.data.database.model

import androidx.room.Entity
import androidx.room.Fts4
import com.marchlab.haema.data.database.model.MovieEntity

@Fts4(contentEntity = MovieEntity::class)
@Entity(tableName = "movie_fts")
class MovieFts