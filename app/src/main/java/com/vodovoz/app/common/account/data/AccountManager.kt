package com.vodovoz.app.common.account.data

import android.content.SharedPreferences
import javax.inject.Inject

class AccountManager @Inject constructor(
    private val sharedPrefs: SharedPreferences
) {

    fun fetchUserId() = when(sharedPrefs.contains(USER_ID)) {
        true -> {
            val userId = sharedPrefs.getLong(USER_ID, 0)
            userId
        }
        false -> {
            null
        }
    }

    fun updateUserId(userId: Long) {
        sharedPrefs.edit().putLong(USER_ID, userId).apply()
    }

    fun removeUserId() {
        sharedPrefs.edit().remove(USER_ID).apply()
    }

    fun removeUserSettings() {
        sharedPrefs.edit().remove(EMAIL).apply()
        sharedPrefs.edit().remove(PASSWORD).apply()
    }

    fun fetchUserSettings() : UserSettings {
        val email = sharedPrefs.getString(EMAIL, "") ?: ""
        val password = sharedPrefs.getString(PASSWORD, "") ?: ""
        return UserSettings(email, password)
    }

    fun updateLastLoginSetting(settings: UserSettings) {
        with(sharedPrefs.edit()) {
            putString(EMAIL, settings.email).apply()
            putString(PASSWORD, settings.password).apply()
        }
    }

    fun clearData() {
        removeUserId()
        removeUserSettings()
    }

    fun isAlreadyLogin() = fetchUserId() != null

    data class UserSettings(
        val email: String,
        val password: String
    )

    companion object {
        private const val USER_ID = "USER_ID"
        private const val EMAIL = "EMAIL"
        private const val PASSWORD = "PASSWORD"
    }

}