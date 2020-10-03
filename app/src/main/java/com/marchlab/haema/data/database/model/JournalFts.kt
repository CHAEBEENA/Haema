package com.marchlab.haema.data.database.model

import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = JournalEntity::class)
@Entity(tableName = "journal_fts")
data class JournalFts(
    val content: String
)