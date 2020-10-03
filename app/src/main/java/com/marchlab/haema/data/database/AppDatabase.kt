package com.marchlab.haema.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.marchlab.haema.data.database.dao.BookDao
import com.marchlab.haema.data.database.dao.JournalDao
import com.marchlab.haema.data.database.dao.MovieDao
import com.marchlab.haema.data.database.model.*

@Database(entities = [
    JournalEntity::class, JournalFts::class,
    BookEntity::class, BookFts::class,
    MovieEntity::class, MovieFts::class
], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun journalDao(): JournalDao

    abstract fun bookDao(): BookDao

    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = instance ?: synchronized(this) {
            Room.databaseBuilder(context, AppDatabase::class.java, "haema.db").build()
        }
    }
}