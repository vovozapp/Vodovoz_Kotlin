package com.vodovoz.app.common.cookie

import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.datastore.DataStoreRepository
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CookieManager @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val accountManager: AccountManager,
) {

    fun fetchCookieSessionId() = runBlocking { dataStoreRepository.getString(COOKIE_SESSION_ID) }
    fun updateCookieSessionId(cookieSessionId: String?) {
        cookieSessionId?.let {
            dataStoreRepository.putString(COOKIE_SESSION_ID, cookieSessionId)
            debugLog { "Cookie updated: $cookieSessionId" }
            setLastEntire()
        }
    }

    fun isAvailableCookieSessionId(): Boolean {
        return dataStoreRepository.contains(COOKIE_SESSION_ID)
    }


    fun removeCookieSessionId() {
        dataStoreRepository.remove(COOKIE_SESSION_ID)
    }

    fun isOldCookie(): Boolean {

        if (!accountManager.isAlreadyLogin()) {
            val currentEntire = System.currentTimeMillis()
            val lastEntire = dataStoreRepository.getLong(COOKIE_LAST_ENTIRE) ?: 0
            val diff = currentEntire - lastEntire
            return diff > COOKIES_LIFE_TIME_IN_MILLIS
        }
        return false
    }

    private fun setLastEntire() {
        dataStoreRepository.putLong(COOKIE_LAST_ENTIRE, System.currentTimeMillis())
    }

    companion object {
        //Cookie Settings
        private const val COOKIE_SESSION_ID = "cookie_SESSION_id"
        private const val COOKIE_LAST_ENTIRE = "COOKIE_LAST_ENTIRE"
        private const val COOKIES_LIFE_TIME_IN_MIN = 120
        private const val COOKIES_LIFE_TIME_IN_MILLIS = COOKIES_LIFE_TIME_IN_MIN * 60 * 1000
    }

}