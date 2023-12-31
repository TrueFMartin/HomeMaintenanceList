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

    @TypeConverter
    fun fromBoolean(intBool: Int): Boolean {
        return intBool != 0
    }
    @TypeConverter
    fun toBoolean(boolean: Boolean): Int {
        return if (boolean)
            1
        else
            0
    }
}