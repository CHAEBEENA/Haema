package com.marchlab.haema.data.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun toDateTime(epochSecond: Long): ZonedDateTime
            = ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneId.of("Asia/Seoul"))

    @TypeConverter
    fun toTimestamp(dateTime: ZonedDateTime): Long = dateTime.toEpochSecond()


    @TypeConverter
    fun toJson(value: List<String>): String = gson.toJson(value, object: TypeToken<List<String>>() {}.type)

    @TypeConverter
    fun fromJson(value: String): List<String> = gson.fromJson(value, object: TypeToken<List<String>>() {}.type)
}