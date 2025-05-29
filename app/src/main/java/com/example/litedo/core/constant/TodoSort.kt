package com.example.litedo.core.constant

enum class TodoSort {
    Name,
    Date;

    companion object {
        fun fromName(name: String?): TodoSort {
            if (name == null) return Name
            return entries.firstOrNull { it.name == name } ?: Name
        }
    }
}