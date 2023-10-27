package com.vodovoz.app.common.token

import com.google.firebase.messaging.FirebaseMessaging
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.util.extensions.debugLog
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class TokenManager @Inject constructor(
    private val accountManager: AccountManager,
    private val repository: MainRepository,
) {

    suspend fun sendFirebaseToken() {
        val token = fetchFirebaseToken()
        val userId = accountManager.fetchAccountId()
        debugLog { "firebase token $token" }
        if (token != null) {
            repository.sendFirebaseToken(userId = userId, token = token)
        }
    }

    private suspend fun fetchFirebaseToken(): String? {
        return suspendCoroutine { continuation ->
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
                .addOnCanceledListener {
                    continuation.resume(null)
                }
        }
    }
}