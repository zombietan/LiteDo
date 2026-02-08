package com.example.litedo.data.local.todo.callback

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.litedo.data.local.todo.database.TodoDatabase
import com.example.litedo.data.local.todo.entity.TodoEntity
import com.example.litedo.data.mapper.toEntity
import com.example.litedo.domain.model.TodoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Provider
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

class TodoCallback(
    private val db: Provider<TodoDatabase>,
    private val scope: CoroutineScope
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        scope.launch {
            val dao = this@TodoCallback.db.get().todoDao()
            val devTodos: List<TodoEntity> = (1..100).map { it ->
                TodoModel(
                    name = "Mock Todo $it",
                    timestamp = Clock.System.now().plus(it.seconds)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                ).toEntity()
            }
            dao.insertTodos(todos = devTodos)
        }
    }
}