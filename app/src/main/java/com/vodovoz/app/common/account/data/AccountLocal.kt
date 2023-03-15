package com.vodovoz.app.common.account.data

import android.content.SharedPreferences
import javax.inject.Inject

class AccountLocal @Inject constructor(
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

    fun isAlreadyLogin() = fetchUserId() != null

    companion object {
        private const val USER_ID = "USER_ID"
    }

}