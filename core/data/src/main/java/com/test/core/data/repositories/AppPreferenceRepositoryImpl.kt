package com.test.core.data.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.test.core.domain.models.UserData
import com.test.core.domain.repositories.AppPreferenceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private const val PREFERENCES_NAME = "AppPreferencesRepository"
private const val USER_KEY = "USER"

private val Context.dataStore by preferencesDataStore(PREFERENCES_NAME)

class AppPreferenceRepositoryImpl @Inject constructor(
    @ApplicationContext val appContext: Context
) : AppPreferenceRepository {
    private val dataStore = appContext.dataStore

    private object PreferencesKeys {
        val USER = stringPreferencesKey(USER_KEY)
    }

    override suspend fun saveUserData(user: UserData) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER] = Gson().toJson(user)
        }
    }

    override suspend fun getUserData(): UserData? {
        val prefs = dataStore.data.first()
        return Gson().fromJson(prefs[PreferencesKeys.USER], UserData::class.java)
    }

    override suspend fun forgetUserData() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.USER)
        }
    }
}