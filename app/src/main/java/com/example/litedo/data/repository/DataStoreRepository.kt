package com.example.litedo.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.litedo.core.constant.DataStoreConst.PREFERENCE_HIDE_COMPLETED_KEY
import com.example.litedo.core.constant.DataStoreConst.PREFERENCE_SORT_KEY
import com.example.litedo.core.constant.TodoSort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferenceKeys {
        val sortKey = stringPreferencesKey(PREFERENCE_SORT_KEY)
        val hideCompletedKey = booleanPreferencesKey(PREFERENCE_HIDE_COMPLETED_KEY)
    }

    suspend fun upsertSorting(value: TodoSort) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.sortKey] = value.name
        }
    }

    val getSorting: Flow<TodoSort> = dataStore.data
        .catch { exception ->
            if (exception is IOException)
                emit(emptyPreferences())
            else
                throw exception
        }
        .map { preferences ->
            val sortData = preferences[PreferenceKeys.sortKey] ?: TodoSort.Date.name
            TodoSort.fromName(sortData)
        }

    suspend fun upsertHideCompleted(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.hideCompletedKey] = value
        }
    }

    val getHideCompleted: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException)
                emit(emptyPreferences())
            else
                throw exception
        }
        .map { preferences ->
            val hideCompletedData = preferences[PreferenceKeys.hideCompletedKey] == true
            hideCompletedData
        }
}