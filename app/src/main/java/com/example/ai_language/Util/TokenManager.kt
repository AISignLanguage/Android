package com.example.ai_language.Util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.ai_language.Util.extensions.datastore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore: DataStore<Preferences> = context.datastore

    val userIdFlow: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[USER_ID]
        }

    suspend fun deleteToken() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID)
        }
    }

    suspend fun saveUserID(userId: Long) {
        if (userId >= 0) {
            dataStore.edit { preferences ->
                preferences[USER_ID] = userId.toString()
            }
        }
    }

    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
    }
}