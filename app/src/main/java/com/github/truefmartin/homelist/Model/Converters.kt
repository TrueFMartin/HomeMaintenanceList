package com.github.truefmartin.homelist.Model

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(time: String?): LocalDateTime? {
        return time?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(time: LocalDateTime?): String? {
        return time?.toString()
    }
}