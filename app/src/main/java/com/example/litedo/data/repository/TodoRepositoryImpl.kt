package com.example.litedo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.litedo.core.constant.TodoSort
import com.example.litedo.data.local.todo.dao.TodoDao
import com.example.litedo.data.mapper.toEntity
import com.example.litedo.data.mapper.toModel
import com.example.litedo.domain.model.TodoModel
import com.example.litedo.domain.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TodoRepositoryImpl(
    private val dao: TodoDao, private val config: PagingConfig
) : TodoRepository {

    // Insert
    override suspend fun insertTodo(todo: TodoModel) {
        withContext(Dispatchers.IO) {
            dao.insertTodo(todo.toEntity())
        }
    }

    // Update
    override suspend fun updateTodo(todo: TodoModel) {
        withContext(Dispatchers.IO) {
            dao.updateTodo(todo.toEntity())
        }
    }

    // Delete
    override suspend fun deleteTodo(todo: TodoModel) {
        withContext(Dispatchers.IO) {
            dao.deleteTodo(todo.toEntity())
        }
    }

    override suspend fun deleteAllCompletedTodo() {
        withContext(Dispatchers.IO) {
            dao.deleteAllCompletedTodo()
        }
    }

    override suspend fun deleteAllTodo() {
        withContext(Dispatchers.IO) {
            dao.deleteAllTodo()
        }
    }

    // Get
    override fun getTodos(
        query: String,
        hideCompleted: Boolean,
        sorting: TodoSort
    ): Flow<PagingData<TodoModel>> {
        return Pager(
            config = config,
            pagingSourceFactory = {
                dao.getTodos(
                    query = query,
                    hideCompleted = hideCompleted,
                    sorting = sorting
                )
            }
        ).flow.map { entities ->
            entities.map { entity ->
                entity.toModel()
            }
        }
    }
}