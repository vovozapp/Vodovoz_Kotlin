package com.vodovoz.app.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: MainRepository,
    private val accountManager: AccountManager
) : ViewModel() {

    fun sendFirebaseToken() {
        viewModelScope.launch {
            val token = fetchFirebaseToken()
            val userId = accountManager.fetchAccountId()
            debugLog { "firebase token $token" }
            if (token != null) {
                repository.sendFirebaseToken(userId = userId, token = token)
            }
        }
    }


    private suspend fun fetchFirebaseToken() : String? {
        return suspendCoroutine {continuation ->
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