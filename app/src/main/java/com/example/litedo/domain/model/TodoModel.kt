package com.example.litedo.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class TodoModel(
    val id: Long = 0,
    val name: String = "",
    val important: Boolean = false,
    val completed: Boolean = false,
    @Contextual
    val timestamp: LocalDateTime = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
)
