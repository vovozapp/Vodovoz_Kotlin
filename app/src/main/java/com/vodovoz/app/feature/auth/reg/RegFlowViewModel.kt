package com.vodovoz.app.feature.auth.reg

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.token.FirebaseTokenManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.config.AuthConfig
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val accountManager: AccountManager,
    private val firebaseTokenManager: FirebaseTokenManager,
    private val likeManager: LikeManager,
) : PagingContractViewModel<RegFlowViewModel.RegState, RegFlowViewModel.RegEvents>(RegState()) {

    fun register(
        firstName: String,
        secondName: String,
        email: String,
        phone: String,
        password: String,
    ) {
        uiStateListener.value = state.copy(loadingPage = true)
        viewModelScope.launch {
            flow { emit(repository.register(firstName, secondName, email, password, phone)) }
                .onEach { response ->
                    when (response) {
                        is ResponseEntity.Success -> {
                            accountManager.updateUserId(response.data)
                            likeManager.updateLikesAfterLogin(response.data)
                            accountManager.updateLastLoginSetting(
                                AccountManager.UserSettings(
                                    email,
                                    password
                                )
                            )

                            uiStateListener.value =
                                state.copy(error = null, loadingPage = false)
                            eventListener.emit(RegEvents.RegSuccess)
                            firebaseTokenManager.sendFirebaseToken()
                        }

                        is ResponseEntity.Error -> {
                            uiStateListener.value =
                                state.copy(loadingPage = false)
                            if (response.errorMessage == AuthConfig.EMAIL_IS_ALREADY_REGISTERED) {
                                eventListener.emit(RegEvents.RegError(AuthConfig.EMAIL_IS_ALREADY_REGISTERED))
                            } else {
                                eventListener.emit(RegEvents.RegError("Ошибка. Попробуйте снова."))
                            }
                        }

                        else -> {}
                    }
                }
                .flowOn(Dispatchers.Default)
                .catch {
                    debugLog { "register error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    sealed class RegEvents : Event {
        object RegSuccess : RegEvents()
        data class RegError(val message: String) : RegEvents()
    }

    data class RegState(
        val items: List<Item> = emptyList(),
    ) : State
}