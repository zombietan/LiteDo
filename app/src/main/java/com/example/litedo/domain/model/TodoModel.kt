package com.example.litedo.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
@Parcelize
data class TodoModel(
    val id: Long = 0,
    val name: String = "",
    val important: Boolean = false,
    val completed: Boolean = false,
    @Contextual
    val timestamp: LocalDateTime = LocalDateTime.now()
) : Parcelable
