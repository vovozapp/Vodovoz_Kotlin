package com.vodovoz.app.feature.profile.notificationsettings

import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.feature.profile.notificationsettings.model.NotSettingsItem
import com.vodovoz.app.feature.profile.notificationsettings.model.NotificationSettingsModel
import com.vodovoz.app.util.extensions.debugLog
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

    fun saveItem(item: NotSettingsItem) {
        val mappedItems = state.data.item?.notSettingsData?.settingsList?.map {
            if(it.id == item.id) {
                item
            } else {
                it
            }
        } ?: state.data.item?.notSettingsData?.settingsList
        uiStateListener.value = state.copy(
            data = state.data.copy(item = state.data.item?.copy(
                notSettingsData = state.data.item?.notSettingsData?.copy(
                    settingsList = mappedItems
                )
            ))
        )
    }

    fun saveChanges(phoneNumber: String?) {
        viewModelScope.launch {
            val userId = accountManager.fetchAccountId() ?: return@launch

            runCatching {
                val uri = ApiConfig.VODOVOZ_URL
                    .toUri()
                    .buildUpon()
                    .encodedPath("newmobile/uvedomleniya_new.php")
                    .appendQueryParameter("action", "sms")
                    .appendQueryParameter("userid", userId.toString())
                    .apply {
                        if (state.data.item?.notSettingsData?.myPhone?.id != null && phoneNumber != null) {
                            appendQueryParameter(state.data.item?.notSettingsData?.myPhone?.id, phoneNumber)
                        }
                    }
                    .apply {
                        if (!state.data.item?.notSettingsData?.settingsList.isNullOrEmpty()) {
                            state.data.item?.notSettingsData?.settingsList?.forEach {
                                appendQueryParameter(it.id, it.active)
                            }
                        }
                    }
                    .build()
                    .toString()

                repository.fetchNotificationSettingsData(uri)
            }
                .onSuccess {
                    uiStateListener.value = state.copy(
                        error = null,
                        loadingPage = false
                    )
                    eventListener.emit(NotSettingsEvents.Success("Сохранение выполнено"))
                }
                .onFailure {
                    uiStateListener.value = state.copy(
                        loadingPage = false,
                        error = it.toErrorState()
                    )
                    eventListener.emit(NotSettingsEvents.Failure("Данные не были изменены"))
                }
        }
    }

    private fun fetchNotificationSettingsData() {
        viewModelScope.launch {
            val userId = accountManager.fetchAccountId() ?: return@launch

            runCatching {
                val uri = ApiConfig.VODOVOZ_URL
                    .toUri()
                    .buildUpon()
                    .encodedPath("newmobile/uvedomleniya_new.php")
                    .appendQueryParameter("action", "detail")
                    .appendQueryParameter("userid", userId.toString())
                    .build()
                    .toString()
                repository.fetchNotificationSettingsData(uri)
            }
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

    sealed class NotSettingsEvents : Event {
        data class Success(val message: String) : NotSettingsEvents()
        data class Failure(val message: String) : NotSettingsEvents()
    }
}