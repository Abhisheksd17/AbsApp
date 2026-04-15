package com.example.common.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.login.AuthResponse
import com.example.model.login.UserResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "user_prefs")
private val TOKEN_KEY = stringPreferencesKey("auth_token")
private val NUMBER_KEY = stringPreferencesKey("num_token")
private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
private val USER_ID_KEY = intPreferencesKey("user_id")
private val DISPLAY_NAME_KEY = stringPreferencesKey("display_name")
private val AVATAR_KEY = stringPreferencesKey("avatar_key")
private val STATUS_TEXT_KEY = stringPreferencesKey("status_text")
class DataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    suspend fun saveNumber(number:String){
        context.dataStore.edit { prefs ->
            prefs[NUMBER_KEY] = number
        }
    }

    suspend fun saveAuthData(user: AuthResponse) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = user.access_token
            prefs[USER_ID_KEY] = user.user_id
        }
    }
    suspend fun saveUserProfile(user: UserResponse) {
        context.dataStore.edit { prefs ->
            prefs[DISPLAY_NAME_KEY] = user.display_name
            user.avatar_key?.let {
                prefs[AVATAR_KEY] = it
            }
            user.status_text?.let {
                prefs[STATUS_TEXT_KEY] = it
            }
        }
    }

    fun getUserProfile(): Flow<UserResponse> {
        return context.dataStore.data.map { prefs ->
            UserResponse(
                display_name = prefs[DISPLAY_NAME_KEY] ?: "",
                avatar_key = prefs[AVATAR_KEY],
                status_text = prefs[STATUS_TEXT_KEY],
                id = prefs[USER_ID_KEY] ?: 0,
                is_online = false,
                last_seen_at = null
            )
        }
    }

    suspend fun getAccessToken(): String? {
        return context.dataStore.data.firstOrNull()?.get(ACCESS_TOKEN_KEY)
    }

    suspend fun getUserId(): Int? {
        return context.dataStore.data.firstOrNull()?.get(USER_ID_KEY)
    }

     fun getNumber(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[NUMBER_KEY]
        }
    }

    fun getTokenFlow(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[ACCESS_TOKEN_KEY]
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }
}