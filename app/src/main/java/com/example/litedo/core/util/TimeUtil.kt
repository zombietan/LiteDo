package com.example.litedo.core.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object TimeUtil {
    fun fromLongToLocalDateTime(millis: Long): LocalDateTime {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    fun fromLocalDateTimeToLong(date: LocalDateTime): Long {
        return date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}