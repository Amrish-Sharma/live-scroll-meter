package com.cb.apps.livescrollmeter.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val TIME_LIMIT_MINUTES = longPreferencesKey("time_limit_minutes")
    }

    val timeLimitMinutes: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[TIME_LIMIT_MINUTES] ?: 10L // Default 10 minutes
    }

    suspend fun setTimeLimit(minutes: Long) {
        context.dataStore.edit { preferences ->
            preferences[TIME_LIMIT_MINUTES] = minutes
        }
    }
}
