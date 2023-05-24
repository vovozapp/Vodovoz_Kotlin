package com.vodovoz.app.feature.profile.notificationsettings

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.feature.profile.notificationsettings.model.NotificationSettingsModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val repository: MainRepository,
    private val accountManager: AccountManager
) : PagingContractViewModel<NotificationSettingsViewModel.NotSettingsState, NotificationSettingsViewModel.NotSettingsEvents>(
    NotSettingsState()
) {

    fun firstLoad() {
        if (!state.isFirstLoad) {
            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            fetchNotificationSettingsData()
        }
    }

    fun refresh() {
        uiStateListener.value =
            state.copy(loadingPage = true)
        fetchNotificationSettingsData()
    }

    private fun fetchNotificationSettingsData() {
        viewModelScope.launch {
            val userId = accountManager.fetchAccountId() ?: return@launch

            runCatching { repository.fetchNotificationSettingsData("detail", userId) }
                .onSuccess {
                    uiStateListener.value = state.copy(
                        data = state.data.copy(item = it),
                        error = null,
                        loadingPage = false
                    )
                }
                .onFailure {
                    uiStateListener.value = state.copy(
                        loadingPage = false,
                        error = it.toErrorState()
                    )
                }
        }
    }


    data class NotSettingsState(
        val item: NotificationSettingsModel? = null,
    ) : State

    sealed class NotSettingsEvents : Event
}