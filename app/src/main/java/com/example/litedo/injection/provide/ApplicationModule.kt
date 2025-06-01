package com.example.litedo.injection.provide

import android.app.Application
import androidx.paging.PagingConfig
import androidx.room.Room
import com.example.litedo.BuildConfig
import com.example.litedo.core.constant.PagingConst
import com.example.litedo.data.local.todo.callback.TodoCallback
import com.example.litedo.data.local.todo.constant.TodoDatabaseConst
import com.example.litedo.data.local.todo.dao.TodoDao
import com.example.litedo.data.local.todo.database.TodoDatabase
import com.example.litedo.data.repository.TodoRepositoryImpl
import com.example.litedo.domain.repository.TodoRepository
import com.example.litedo.injection.annotation.ApplicationScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    @Provides
    @Singleton
    fun provideTodoDatabase(
        application: Application,
        todoCallback: TodoCallback
    ): TodoDatabase {
        val builder = Room.databaseBuilder(
            context = application,
            klass = TodoDatabase::class.java,
            name = TodoDatabaseConst.NAME
        )
        if (BuildConfig.DEBUG) {
            builder.addCallback(todoCallback)
        }
        return builder
            .addMigrations()
            .build()
    }

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Provides
    @Singleton
    fun provideTodoCallback(
        todoDatabase: Provider<TodoDatabase>,
        @ApplicationScope applicationScope: CoroutineScope
    ): TodoCallback = TodoCallback(
        db = todoDatabase,
        scope = applicationScope
    )


    @Provides
    @Singleton
    fun provideTodoDao(todoDatabase: TodoDatabase): TodoDao = todoDatabase.todoDao()

    @Provides
    @Singleton
    fun providePagingConfig(): PagingConfig =
        PagingConfig(
            pageSize = PagingConst.PAGE_SIZE,
            prefetchDistance = PagingConst.PREFETCH_DISTANCE,
            maxSize = PagingConst.MAX_SIZE,
            enablePlaceholders = PagingConst.ENABLE_PLACEHOLDERS
        )

    @Provides
    @Singleton
    fun provideTodoRepository(todoDao: TodoDao, pagingConfig: PagingConfig): TodoRepository =
        TodoRepositoryImpl(dao = todoDao, config = pagingConfig)

    @Provides
    @Singleton
    fun provideTimberDebugTree(): Timber.DebugTree = Timber.DebugTree()
}