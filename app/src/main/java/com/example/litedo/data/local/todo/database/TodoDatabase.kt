package com.example.litedo.data.local.todo.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.litedo.data.local.todo.constant.TodoDatabaseConst
import com.example.litedo.data.local.todo.converter.TodoConverter
import com.example.litedo.data.local.todo.dao.TodoDao
import com.example.litedo.data.local.todo.entity.TodoEntity

@TypeConverters(TodoConverter::class)
@Database(
    entities = [TodoEntity::class],
    version = TodoDatabaseConst.VERSION,
    exportSchema = TodoDatabaseConst.ROOM_DB_EXPORT_SCHEMA
)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}