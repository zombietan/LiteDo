package com.example.litedo.data.mapper

import com.example.litedo.data.local.todo.entity.TodoEntity
import com.example.litedo.domain.model.TodoModel

// Model
fun TodoEntity.toModel(): TodoModel {
    return TodoModel(
        id = id,
        name = name,
        important = important,
        completed = completed,
        timestamp = timestamp
    )
}

// Entity
fun TodoModel.toEntity(): TodoEntity {
    return TodoEntity(
        id = id,
        name = name,
        important = important,
        completed = completed,
        timestamp = timestamp
    )
}