package com.example.litedo.presentation.navigation

import android.net.Uri
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import com.example.litedo.domain.model.TodoModel
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

private object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val str = value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        encoder.encodeString(str)
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

}

private val module = SerializersModule {
    contextual(LocalDateTime::class, LocalDateTimeSerializer)
}

private val json = Json {
    serializersModule = module
}