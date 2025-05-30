package com.example.litedo.domain.repository

import com.example.litedo.core.constant.TodoSort
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {

    suspend fun upsertSorting(value: TodoSort)

    val getSorting: Flow<TodoSort>

    suspend fun upsertHideCompleted(value: Boolean)

    val getHideCompleted: Flow<Boolean>
}