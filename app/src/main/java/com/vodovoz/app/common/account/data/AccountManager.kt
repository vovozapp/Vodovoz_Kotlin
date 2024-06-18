package com.vodovoz.app.common.account.data

import com.vodovoz.app.common.datastore.DataStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountManager @Inject constructor(
//    private val sharedPrefs: SharedPreferences,
    private val dataStoreRepository: DataStoreRepository,
) {

    private val accountIdListener = MutableStateFlow<Long?>(null)
    fun observeAccountId() = accountIdListener.asStateFlow()

    fun fetchAccountId(): Long? {
        val id = accountIdListener.value ?: fetchUserId()
        accountIdListener.value = id
        return id
    }

    private fun fetchUserId() = dataStoreRepository.getLong(USER_ID)

    fun updateUserId(userId: Long) {
        dataStoreRepository.putLong(USER_ID, userId)
        accountIdListener.value = userId
    }

    fun removeUserId() {
        dataStoreRepository.remove(USER_ID)
        accountIdListener.value = null
    }

    fun fetchUserToken() =
        dataStoreRepository.getString(USER_TOKEN)

    fun updateUserToken(userToken: String) {
        dataStoreRepository.putString(USER_TOKEN, userToken)
    }

    fun removeUserToken() {
        dataStoreRepository.remove(USER_TOKEN)
    }

    fun fetchUserSettings(): UserSettings {
        val email = dataStoreRepository.getString(EMAIL) ?: ""
        val password = dataStoreRepository.getString(PASSWORD) ?: ""
        return UserSettings(email, password)
    }

    fun updateLastLoginSetting(settings: UserSettings) {
        with(dataStoreRepository) {
            putString(EMAIL, settings.email)
            putString(PASSWORD, settings.password)
        }
    }

    fun saveUseBio(use: Boolean) {
        dataStoreRepository.putBoolean(USE_BIO, use)
    }

    fun fetchUseBio(): Boolean {
        return dataStoreRepository.getBoolean(USE_BIO) ?: false
    }

    fun isAlreadyLogin() = fetchUserId() != null

    fun reportYandexMetrica(text: String, eventParam: String? = null) {
        val eventParameters = "{\"UserID\":\"${accountIdListener.value ?: "0"}\"" +
                if (eventParam != null) ",$eventParam}" else "}"
//        YandexMetrica.reportEvent(text, eventParameters) //todo release
    }

    data class UserSettings(
        val email: String,
        val password: String,
    )

    companion object {
        private const val USER_ID = "USER_ID"
        private const val USER_TOKEN = "USER_TOKEN"
        private const val EMAIL = "EMAIL"
        private const val PASSWORD = "PASSWORD"
        private const val USE_BIO = "USE_BIO"
    }

}