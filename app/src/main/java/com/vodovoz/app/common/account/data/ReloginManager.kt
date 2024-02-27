package com.vodovoz.app.common.account.data

import com.vodovoz.app.common.cookie.CookieManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.UserReloginEntity
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReloginManager @Inject constructor(
    private val repository: MainRepository,
    private val accountManager: AccountManager,
    private val cookieManager: CookieManager,
) {

    private val _userReloginEnded = MutableStateFlow<ReloginState>(ReloginState.ReloginInitial)
    val userReloginEnded = _userReloginEnded.asStateFlow()

    fun reloginUser() {
        val userId = accountManager.fetchAccountId()
        val userToken = accountManager.fetchUserToken()
        if (userId != null && !userToken.isNullOrEmpty()) {
            CoroutineScope(Dispatchers.Main.immediate).launch {
                flow {
                    emit(repository.relogin(userId, userToken))
                }.catch {
                    _userReloginEnded.value = ReloginState.ReloginError(it.message.toString())
                }
                    .collect {
                        if (it.isSuccessful) {
                            val userRelogin = it.body() ?: UserReloginEntity(false)
                            debugLog { userRelogin.toString() }
                            if (userRelogin.isAuthorized) {
                                val newCookie = it.headers()["Set-Cookie"] ?: ""
                                if (newCookie.isNotEmpty()) {
                                    cookieManager.updateCookieSessionId(newCookie)
                                }
                            } else {
                                accountManager.removeUserId()
                                accountManager.removeUserToken()
                                cookieManager.removeCookieSessionId()
                            }
                            _userReloginEnded.value = ReloginState.ReloginSuccess

                        } else {
                            _userReloginEnded.value = ReloginState.ReloginError(
                                it.errorBody()?.string() ?: "Unknown error"
                            )
                        }
                    }
            }
        } else {
            _userReloginEnded.value = ReloginState.ReloginSuccess
        }
    }

    sealed class ReloginState {

        object ReloginInitial : ReloginState()
        data class ReloginError(val error: String) : ReloginState()
        object ReloginSuccess : ReloginState()
    }
}