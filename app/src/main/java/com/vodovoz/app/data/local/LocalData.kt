package com.vodovoz.app.data.local

import android.content.Context
import com.vodovoz.app.data.model.common.LastLoginDataEntity
import io.reactivex.rxjava3.core.Single

class LocalData(
    private val context: Context
) : LocalDataSource {

    companion object {
        private const val ACCOUNT_SETTINGS = "account_settings"
        private const val USER_ID = "USER_ID"
        private const val EMAIL = "EMAIL"
        private const val PASSWORD = "PASSWORD"
    }

    private val accountSettings = context.getSharedPreferences(ACCOUNT_SETTINGS, Context.MODE_PRIVATE);


    override fun fetchLastLoginData(): LastLoginDataEntity {
        val email = when(accountSettings.contains(EMAIL)) {
            true -> accountSettings.getString(EMAIL, null)
            false -> null
        }

        val password = when(accountSettings.contains(PASSWORD)) {
            true -> accountSettings.getString(PASSWORD, null)
            false -> null
        }

        return LastLoginDataEntity(
            email = email,
            password = password
        )
    }

    override fun updateLastLoginData(
        lastLoginDataEntity: LastLoginDataEntity
    ) {
        with(accountSettings.edit()) {
            lastLoginDataEntity.email?.let { putString(EMAIL, it).apply() }
            lastLoginDataEntity.password?.let { putString(PASSWORD, it).apply() }
        }
    }

    override fun fetchUserId()  = when(accountSettings.contains(USER_ID)) {
        true -> accountSettings.getLong(USER_ID, 0)
        false -> null
    }

    override fun updateUserId(userId: Long) {
        accountSettings.edit().putLong(USER_ID, userId).apply()
    }

    override fun removeUserId() {
        accountSettings.edit().remove(USER_ID).apply()
    }

    override fun isAlreadyLogin() = fetchUserId() != null

}