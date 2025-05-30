package com.example.litedo.presentation.navigation

import android.net.Uri
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import com.example.litedo.domain.model.TodoModel
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.serializers.LocalDateTimeIso8601Serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

object CustomNavType {
    val TodoModelType = object : NavType<TodoModel>(isNullableAllowed = false) {

        override fun get(bundle: SavedState, key: String): TodoModel? {
            return json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): TodoModel {
            return json.decodeFromString(Uri.decode(value))
        }

        override fun serializeAsValue(value: TodoModel): String {
            return Uri.encode(json.encodeToString(value))
        }

        override fun put(bundle: SavedState, key: String, value: TodoModel) {
            bundle.putString(key, json.encodeToString(value))
        }
    }
}

private val module = SerializersModule {
    contextual(LocalDateTime::class, LocalDateTimeIso8601Serializer)
}

private val json = Json {
    serializersModule = module
}