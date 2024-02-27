package com.vodovoz.app.common.datastore

import android.content.Context
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(
    name = "datastore",
    produceMigrations = { context ->
        listOf(
            SharedPreferencesMigration(
                context = context,
                sharedPreferencesName = "shared_prefs"
            ),
            SharedPreferencesMigration(
                context = context,
                sharedPreferencesName = "COOKIE_SETTINGS"
            )
        )
    }
)

class DataStoreRepositoryImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
) : DataStoreRepository {
    override fun putString(key: String, value: String) {
        val prefKey = stringPreferencesKey(key)
        runBlocking {
            context.dataStore.edit {
                it[prefKey] = value
            }
        }
    }

    override fun getString(key: String): String? {
        return try {
            val prefKey = stringPreferencesKey(key)
            runBlocking { context.dataStore.data.first()[prefKey] }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun putBoolean(key: String, value: Boolean) {
        val prefKey = booleanPreferencesKey(key)
        runBlocking {
            context.dataStore.edit {
                it[prefKey] = value
            }
        }
    }

    override fun getBoolean(key: String): Boolean? {
        return try {
            val prefKey = booleanPreferencesKey(key)
            runBlocking { context.dataStore.data.first()[prefKey] }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun putInt(key: String, value: Int) {
        val prefKey = intPreferencesKey(key)
        runBlocking {
            context.dataStore.edit {
                it[prefKey] = value
            }
        }
    }

    override fun getInt(key: String): Int? {
        return try {
            val prefKey = intPreferencesKey(key)
            runBlocking { context.dataStore.data.first()[prefKey] }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun putLong(key: String, value: Long) {
        val prefKey = longPreferencesKey(key)
        runBlocking {
            context.dataStore.edit {
                it[prefKey] = value
            }
        }
    }

    override fun getLong(key: String): Long? {
        return try {
            val prefKey = longPreferencesKey(key)
            runBlocking { context.dataStore.data.first()[prefKey] }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun remove(key: String) {
        val prefKey = stringPreferencesKey(key)
        runBlocking {
            context.dataStore.edit {
                if (it.contains(prefKey)) {
                    it.remove(prefKey)
                }
            }
        }
    }

    override fun contains(key: String): Boolean {
        val prefKey = stringPreferencesKey(key)
        return runBlocking { context.dataStore.data.first().contains(prefKey) }
    }
}

