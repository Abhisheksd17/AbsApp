package com.example.common.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
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




private val ACCESS_TOKEN= stringPreferencesKey("access_token")
private val USER_ID = intPreferencesKey("user_id")
private val DISPLAY_NAME = stringPreferencesKey("display_name")
private val STATUS_TEXT = stringPreferencesKey("status_text")
private val PROFILE_URL = stringPreferencesKey("profile_url")

class DataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {



    suspend fun saveUserData(
        authResponse: AuthResponse
    ) {

        context.dataStore.edit { pref ->

            pref[ACCESS_TOKEN] = authResponse.access_token

            pref[USER_ID] = authResponse.user_id

            pref[DISPLAY_NAME] = authResponse.display_name

            authResponse.status_text?.let {
                pref[STATUS_TEXT] = it
            } ?: pref.remove(STATUS_TEXT)

            authResponse.profile_url?.let {
                pref[PROFILE_URL] = it
            } ?: pref.remove(PROFILE_URL)



        }
    }

    suspend fun saveNumber(number:String){
        context.dataStore.edit { prefs ->
            prefs[NUMBER_KEY] = number
        }
    }



    fun getUserProfile(): Flow<UserResponse> {
        return context.dataStore.data.map { prefs ->
            UserResponse(
                display_name = prefs[DISPLAY_NAME] ?: "",
                avatar_key = prefs[PROFILE_URL],
                status_text = prefs[STATUS_TEXT],
                id = prefs[USER_ID] ?: 0,
                is_online = false,
                last_seen_at = null
            )
        }
    }

    suspend fun getAccessToken(): String? {
        return context.dataStore.data.firstOrNull()?.get(ACCESS_TOKEN)
    }

    suspend fun getUserId(): Int? {
        return context.dataStore.data.firstOrNull()?.get(USER_ID)
    }

     fun getNumber(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[NUMBER_KEY]
        }
    }

    fun getTokenFlow(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[ACCESS_TOKEN]
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }
}