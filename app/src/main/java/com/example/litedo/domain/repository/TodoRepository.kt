package com.example.litedo.domain.repository

import androidx.paging.PagingData
import com.example.litedo.core.constant.TodoSort
import com.example.litedo.domain.model.TodoModel
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    // Insert
    suspend fun insertTodo(todo: TodoModel)

    suspend fun insertTodos(todos: List<TodoModel>)

    // Update
    suspend fun updateTodo(todo: TodoModel)

    // Delete
    suspend fun deleteTodo(todo: TodoModel)

    suspend fun deleteAllCompletedTodo()

    suspend fun deleteAllTodo()

    // Get
    fun getTodos(
        query: String,
        hideCompleted: Boolean,
        sorting: TodoSort
    ): Flow<PagingData<TodoModel>>
}