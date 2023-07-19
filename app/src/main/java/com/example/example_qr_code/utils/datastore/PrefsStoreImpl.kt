package com.example.example_qr_code.utils.datastore

import android.content.Context
import android.graphics.Bitmap
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject


private const val USER_PREFERENCES_NAME = "photo_collapse_data_store"

class PrefsStoreImpl @Inject constructor(@ApplicationContext context: Context) : PrefsStore {

    private val Context.dataStore by preferencesDataStore(
        name = USER_PREFERENCES_NAME,
        produceMigrations = { context ->
            listOf(SharedPreferencesMigration(context, USER_PREFERENCES_NAME)
        )
    })

    private val dataStore = context.dataStore

    override fun isOpenFirstTime() = dataStore.data.map {
        it[PreferencesKeys.IS_OPEN_FIRST_TIME] ?: false
    }

    override suspend fun openFirstTime(states: Boolean) {
        dataStore.edit {
            it[PreferencesKeys.IS_OPEN_FIRST_TIME] = states
        }
    }

    private object PreferencesKeys {
        val IS_OPEN_FIRST_TIME = booleanPreferencesKey("is_open_first_time")
    }
}