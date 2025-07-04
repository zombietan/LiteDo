package com.example.litedo.data.local.todo.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.litedo.data.local.todo.constant.TodoDatabaseConst.TABLE_TODO
import kotlinx.datetime.LocalDateTime

@Entity(tableName = TABLE_TODO)
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo("_id") val id: Long = 0,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("important") val important: Boolean,
    @ColumnInfo("completed") val completed: Boolean,
    @ColumnInfo("timestamp") val timestamp: LocalDateTime
)
