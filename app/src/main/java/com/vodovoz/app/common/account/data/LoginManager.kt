package com.vodovoz.app.common.account.data

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginManager @Inject constructor(
    private val sharedPrefs: SharedPreferences,
) {

    fun updateLastAuthPhone(phone: String) {
        sharedPrefs.edit().putString(PHONE, phone).apply()
    }

    fun fetchLastAuthPhone() = when (sharedPrefs.contains(PHONE)) {
        true -> sharedPrefs.getString(PHONE, "") ?: ""
        false -> ""
    }

    fun updateLastRequestCodeDate(time: Long) {
        sharedPrefs.edit().putLong(LAST_REQUEST_CODE_DATE, time).apply()
    }

    fun fetchLastRequestCodeDate() = when (sharedPrefs.contains(LAST_REQUEST_CODE_DATE)) {
        true -> sharedPrefs.getLong(LAST_REQUEST_CODE_DATE, 0)
        false -> 0L
    }

    fun updateLastRequestCodeTimeOut(time: Int) {
        sharedPrefs.edit().putInt(LAST_REQUEST_CODE_TIME_OUT, time).apply()
    }

    fun fetchLastRequestCodeTimeOut() = when (sharedPrefs.contains(LAST_REQUEST_CODE_TIME_OUT)) {
        true -> sharedPrefs.getInt(LAST_REQUEST_CODE_TIME_OUT, 0)
        false -> 0
    }

    companion object {
        private const val PHONE = "PHONE"
        private const val LAST_REQUEST_CODE_DATE = "LAST_REQUEST_CODE_DATE"
        private const val LAST_REQUEST_CODE_TIME_OUT = "LAST_REQUEST_CODE_TIME_OUT"
    }
}