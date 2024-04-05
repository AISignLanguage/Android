package com.example.ai_language.Util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.ai_language.Util.extensions.datastore
import com.kakao.sdk.auth.model.OAuthToken
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


    //토큰만 걸러서 뽑아줌
    val findToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[ACCESS_TOKEN]
        }

    suspend fun deleteToken() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
        }
    }

    suspend fun saveToken(token: OAuthToken?) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = token.toString()
        }
    }

    fun getAccessToken(): Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[ACCESS_TOKEN]
        }
    fun getRefreshToken(): Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[REFRESH_TOKEN]
        }

    // JWT 토큰 함수
    suspend fun saveJwtToken(jwtAccessToken: String, jwtRefreshToken: String) {
        dataStore.edit { preferences ->
            preferences[JWT_ACCESS_TOKEN] = jwtAccessToken
            preferences[JWT_REFRESH_TOKEN] = jwtRefreshToken
        }
    }

    suspend fun deleteJwtAccessToken() {
        dataStore.edit { preferences ->
            preferences.remove(JWT_ACCESS_TOKEN)
        }
    }
    suspend fun deleteJwtRefreshToken() {
        dataStore.edit { preferences ->
            preferences.remove(JWT_REFRESH_TOKEN)
        }
    }

    fun getJwtAccessToken(): Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[JWT_ACCESS_TOKEN]
        }

    fun getJwtRefreshToken(): Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[JWT_REFRESH_TOKEN]
        }

    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")

        private val JWT_ACCESS_TOKEN = stringPreferencesKey("jwt_access_token")
        private val JWT_REFRESH_TOKEN = stringPreferencesKey("jwt_refresh_token")
    }
}