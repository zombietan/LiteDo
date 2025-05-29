package com.example.litedo.injection.provide

import com.example.litedo.injection.annotation.TimeFormatter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import java.time.format.DateTimeFormatter
import java.util.Locale

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    @ViewModelScoped
    @TimeFormatter
    fun provideTimeFormatter(): DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy, MMMM d, HH:mm", Locale.getDefault())
}