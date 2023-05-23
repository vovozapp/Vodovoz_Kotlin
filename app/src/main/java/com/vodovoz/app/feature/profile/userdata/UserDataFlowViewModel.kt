package com.vodovoz.app.feature.profile.userdata

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.*
import com.vodovoz.app.common.media.MediaManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.user.UpdateUserDataResponseJsonParser.parseUpdateUserDataResponse
import com.vodovoz.app.data.parser.response.user.UserDataResponseJsonParser.parseUserDataResponse
import com.vodovoz.app.mapper.UserDataMapper.mapToUI
import com.vodovoz.app.ui.model.UserDataUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UserDataFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val savedStateHandle: SavedStateHandle,
    private val accountManager: AccountManager,
    private val mediaManager: MediaManager
) : PagingContractViewModel<UserDataFlowViewModel.UserDataState, UserDataFlowViewModel.UserDataEvents>(
    UserDataState()
) {

    init {
        viewModelScope.launch {
            mediaManager
                .observeAvatarImage()
                .collect {
                    if (it != null) {
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                item = state.data.item?.copy(
                                    avatar = it.path
                                )
                            )
                        )

                        addAvatar(it)
                    }
                }
        }
    }

    private fun addAvatar(image: File) {
        val userId = accountManager.fetchAccountId() ?: return
        viewModelScope.launch {
            flow { emit(repository.addAvatar(userId, image)) }
                .flowOn(Dispatchers.IO)
                .onEach {
                    if (!it.isSuccessful) {
                        clearAvatarState()
                    } else {
                        eventListener.emit(UserDataEvents.UpdateProfile)
                    }
                }
                .catch {
                    debugLog { "add avatar error ${it.localizedMessage}" }
                    clearAvatarState()
                    uiStateListener.value =
                        state.copy(
                            error =  it.toErrorState(),
                            loadingPage = false,
                            loadMore = false,
                            bottomItem = null
                        )
                }

                .collect()
        }
    }


    private fun clearAvatarState() {
        mediaManager.removeAvatarImage()

        uiStateListener.value =
            state.copy(
                data = state.data.copy(
                    item = state.data.item?.copy(
                        avatar = ""
                    )
                )
            )
    }

    fun fetchUserData() {
        viewModelScope.launch {
            val userId = accountManager.fetchAccountId() ?: return@launch

            flow { emit(repository.fetchUserData(userId)) }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseUserDataResponse()
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(
                                item = data,
                                canChangeBirthDay = data.birthday.isNotEmpty().not()
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
                .catch {
                    debugLog { "fetch user data error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
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

            flow {
                emit(
                    repository.updateUserData(
                        userId = userId,
                        firstName = firstName,
                        secondName = secondName,
                        password = password,
                        phone = phone,
                        sex = sex,
                        birthday = birthday,
                        email = email
                    )
                )
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
                                    gender = if (sex == "Мужской") {
                                        Gender.MALE
                                    } else {
                                        Gender.FEMALE
                                    },
                                    phone = phone
                                ),
                                canChangeBirthDay = birthday.isNotEmpty().not()
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
                .catch {
                    debugLog { "update user data error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun setUserGender(gender: Gender) {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                item = state.data.item?.copy(
                    gender = gender,
                )
            )
        )
    }

    fun navigateToGenderChoose() {
        viewModelScope.launch {
            val name = state.data.item?.gender?.name ?: return@launch
            eventListener.emit(UserDataEvents.NavigateToGenderChoose(name))
        }
    }

    fun onBirthdayClick() {
        val birthday = state.data.item?.birthday
        val canChange = state.data.canChangeBirthDay
        viewModelScope.launch {
            if (birthday != null && birthday == "Не указано" && canChange) {
                eventListener.emit(UserDataEvents.ShowDatePicker)
            } else {
                eventListener.emit(UserDataEvents.UpdateUserDataEvent("Это поле нельзя изменить!"))
            }
        }
    }


    sealed class UserDataEvents : Event {
        data class UpdateUserDataEvent(val message: String) : UserDataEvents()
        data class NavigateToGenderChoose(val gender: String) : UserDataEvents()
        object ShowDatePicker : UserDataEvents()
        object UpdateProfile : UserDataEvents()
    }

    data class UserDataState(
        val item: UserDataUI? = null,
        val canChangeBirthDay: Boolean = true
    ) : State

}