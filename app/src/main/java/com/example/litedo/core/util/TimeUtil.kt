package com.example.litedo.core.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

object TimeUtil {
    private val zone = TimeZone.currentSystemDefault()
    fun fromLongToLocalDateTime(millis: Long): LocalDateTime {
        return Instant.fromEpochMilliseconds(millis).toLocalDateTime(zone)
    }

    fun fromLocalDateTimeToLong(date: LocalDateTime): Long {
        return date.toInstant(zone).toEpochMilliseconds()
    }
}