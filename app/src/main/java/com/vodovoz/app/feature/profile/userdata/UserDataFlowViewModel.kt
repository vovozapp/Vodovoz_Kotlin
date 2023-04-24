package com.vodovoz.app.feature.profile.userdata

import android.service.autofill.UserData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.*
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.user.UpdateUserDataResponseJsonParser.parseUpdateUserDataResponse
import com.vodovoz.app.data.parser.response.user.UserDataResponseJsonParser.parseUserDataResponse
import com.vodovoz.app.feature.profile.ProfileFlowViewModel
import com.vodovoz.app.feature.profile.viewholders.models.ProfileHeader
import com.vodovoz.app.mapper.UserDataMapper.mapToUI
import com.vodovoz.app.ui.fragment.user_data.Gender
import com.vodovoz.app.ui.model.UserDataUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDataFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val savedStateHandle: SavedStateHandle,
    private val accountManager: AccountManager
) : PagingContractViewModel<UserDataFlowViewModel.UserDataState, UserDataFlowViewModel.UserDataEvents>(UserDataState()) {

    fun fetchUserData() {
        viewModelScope.launch {
            val userId = accountManager.fetchAccountId() ?: return@launch

            flow { emit(repository.fetchUserData(userId)) }
                .catch {
                    debugLog { "fetch user data error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseUserDataResponse()
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(
                                item = data
                            ),
                            error = null
                        )
                    } else {
                        state.copy(
                            loadingPage = false,
                            error = ErrorState.Error()
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun updateUserData(
        firstName: String,
        secondName: String,
        sex: String,
        birthday: String,
        email: String,
        phone: String,
        password: String
    ) {
        viewModelScope.launch {
            val userId = accountManager.fetchAccountId() ?: return@launch

            flow { emit(repository.updateUserData(
                userId = userId,
                firstName = firstName,
                secondName = secondName,
                password = password,
                phone = phone,
                sex = sex,
                birthday = birthday,
                email = email
            )) }
                .catch {
                    debugLog { "update user data error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseUpdateUserDataResponse()
                    if (response is ResponseEntity.Success) {
                        uiStateListener.value = state.copy(
                            loadingPage = false,
                            data = state.data.copy(
                                item = state.data.item?.copy(
                                    id = userId,
                                    firstName = firstName,
                                    secondName = secondName,
                                    email = email,
                                    gender = Gender.valueOf(sex),
                                    phone = phone
                                )
                            ),
                            error = null
                        )
                        eventListener.emit(UserDataEvents.UpdateUserDataEvent("Данные успешно изменены"))
                    } else {
                        uiStateListener.value = state.copy(
                            loadingPage = false,
                            error = ErrorState.Error()
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }


    sealed class UserDataEvents : Event {
        data class UpdateUserDataEvent(val message: String) : UserDataEvents()
    }

    data class UserDataState(
        val item: UserDataUI? = null
    ) : State

}