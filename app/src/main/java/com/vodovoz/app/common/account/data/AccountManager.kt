package com.vodovoz.app.common.account.data

import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountManager @Inject constructor(
    private val sharedPrefs: SharedPreferences
) {

    private val accountIdListener = MutableStateFlow<Long?>(null)
    fun observeAccountId() = accountIdListener.asStateFlow()

    fun fetchAccountId() : Long? {
        val id = accountIdListener.value ?: fetchUserId()
        accountIdListener.value = id
        return id
    }

    private fun fetchUserId() = when (sharedPrefs.contains(USER_ID)) {
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
        accountIdListener.value = userId
    }

    fun removeUserId() {
        sharedPrefs.edit().remove(USER_ID).apply()
        accountIdListener.value = null
    }

    fun removeUserSettings() {
        sharedPrefs.edit().remove(EMAIL).apply()
        sharedPrefs.edit().remove(PASSWORD).apply()
    }

    fun fetchUserSettings(): UserSettings {
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