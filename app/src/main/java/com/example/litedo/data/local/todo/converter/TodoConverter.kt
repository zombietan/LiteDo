package com.example.litedo.data.local.todo.converter

import androidx.room.TypeConverter
import com.example.litedo.core.util.TimeUtil
import kotlinx.datetime.LocalDateTime

class TodoConverter {
    @TypeConverter
    fun localDateTimeToLong(value: LocalDateTime): Long {
        return TimeUtil.fromLocalDateTimeToLong(value)
    }

    @TypeConverter
    fun longToLocalDateTime(value: Long): LocalDateTime {
        return TimeUtil.fromLongToLocalDateTime(value)
    }
}