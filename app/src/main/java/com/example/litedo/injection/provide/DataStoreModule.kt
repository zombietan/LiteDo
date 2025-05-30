package com.example.litedo.injection.provide

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.litedo.core.constant.DataStoreConst.PREFERENCE_NAME
import com.example.litedo.data.repository.DataStoreRepositoryImpl
import com.example.litedo.domain.repository.DataStoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create {
        context.preferencesDataStoreFile(PREFERENCE_NAME)
    }

    @Provides
    @Singleton
    fun provideDataStoreRepository(
        dataStore: DataStore<Preferences>
    ): DataStoreRepository = DataStoreRepositoryImpl(dataStore)
}