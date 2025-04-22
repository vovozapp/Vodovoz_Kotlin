package com.vodovoz.app.feature.profile.userdata

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.common.media.MediaManager
import com.vodovoz.app.common.resources.ResourcesProvider
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.domain.general.model.UserNotLoginException
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.preorder.model.FieldUi
import com.vodovoz.app.feature.preorder.model.checkFields
import com.vodovoz.app.feature.preorder.model.mapToDomain
import com.vodovoz.app.feature.preorder.model.toUi
import com.vodovoz.app.feature.preorder.model.updateFieldAndResetErrors
import com.vodovoz.app.feature.preorder.model.updateFieldValueAndResetErrors
import com.vodovoz.app.mapper.UserDataMapper.mapToUI
import com.vodovoz.app.ui.model.UserDataUI
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.singleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@Stable
@HiltViewModel
class UserDataFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val accountManager: AccountManager,
    private val mediaManager: MediaManager,
    private val vodovozServiceRepository: VodovozServiceRepository,
    private val resourcesProvider: ResourcesProvider,
) : PagingContractViewModel<UserDataFlowViewModel.UserDataState, UserDataFlowViewModel.UserDataEvents>(
    UserDataState()
) {


    init {
        viewModelScope.launch {
            mediaManager
                .observeAvatarImage()
                .collect { imageFile ->
                    imageFile ?: return@collect

                    updateUserAvatar(imageFile)
                    mediaManager.removeAvatarImage()
                }
        }
    }

    private fun addAvatar(image: File) {
        val userId = accountManager.fetchAccountId() ?: return
        viewModelScope.launch {
            flow { emit(repository.addAvatar(userId, image)) }
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
                            error = it.toErrorState(),
                            loadingPage = false,
                            loadMore = false,
                            bottomItem = null
                        )
                }.collect()
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

    fun fetchUserData() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(uiState = UserDataUiState.Loading)
        }
        val userDataResult = vodovozServiceRepository.getUserData().singleResult()

        userDataResult.onSuccess { userData ->
            uiStateListener.updateData { s ->
                val photoModel = userData.photo
                s.copy(
                    title = userData.title,
                    fields = userData.fields.map { field -> field.toUi() },
                    photo = photoModel.imageUrl,
                    photoDescription = photoModel.description,
                    photoTitle = photoModel.title,
                    uiState = UserDataUiState.Success
                )
            }

        }.onFailure { t ->
            if (t is UserNotLoginException && t.errorData != null) {
                eventListener.emit(UserDataEvents.GoBack)
            } else {
                uiStateListener.updateData { s ->
                    s.copy(uiState = UserDataUiState.Error)
                }
            }
        }


        val userId = accountManager.fetchAccountId() ?: return@launch

        flow { emit(repository.fetchUserData(userId)) }
            .onEach {
//                    val response = it.parseUserDataResponse()
                uiStateListener.value = if (it is ResponseEntity.Success) {
                    val data = it.data.mapToUI()
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


    fun updateUserData(
        firstName: String,
        secondName: String,
        sex: String,
        birthday: String,
        email: String,
        phone: String,
        password: String,
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
                .onEach { response ->
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

    fun showPassword() {
        uiStateListener.value =
            state.copy(data = state.data.copy(showPassword = !state.data.showPassword))
    }

    fun navigateBack() = viewModelScope.launch {
        eventListener.emit(UserDataEvents.GoBack)
    }

    fun changeFieldValue(field: FieldUi, newValue: String) = viewModelScope.launch {
        val updatedFields = dataState.fields.updateFieldValueAndResetErrors(field, newValue)

        updatedFields.checkFields(false) { fields, isValid ->
            uiStateListener.updateData { s ->
                s.copy(
                    fields = fields,
                    buttonEnabled = fields.checkFields()
                )
            }
        }
    }

    private fun updateUserAvatar(imageFile: File) = viewModelScope.launch {
        val updateUserAvatarResult =
            vodovozServiceRepository.updateUserAvatar(imageFile).singleResult()
        updateUserAvatarResult.onSuccess { message ->
            uiStateListener.updateData { s ->
                s.copy(photo = imageFile.path)
            }
            eventListener.emit(UserDataEvents.UpdateProfile)
            eventListener.emit(UserDataEvents.ShowSnackbar(message))
        }.onFailure {
            val message = it.message ?: return@onFailure
            eventListener.emit(UserDataEvents.ShowSnackbar(message))
        }
    }

    fun updateUserData() = viewModelScope.launch {
        val updateUserDataResult =
            vodovozServiceRepository.updateUserData(dataState.fields.mapToDomain()).singleResult()
        updateUserDataResult.onSuccess { message ->
            eventListener.emit(UserDataEvents.UpdateProfile)
            eventListener.emit(UserDataEvents.ShowSnackbar(message))
            uiStateListener.updateData { s ->
                s.copy(buttonEnabled = false)
            }
        }.onFailure {
            eventListener.emit(
                UserDataEvents.ShowSnackbar(resourcesProvider.getString(R.string.update_user_data_error))
            )
            uiStateListener.updateData { s ->
                s.copy(buttonEnabled = false)
            }
        }
    }

    fun logout() = viewModelScope.launch {
        //todo - make logout
    }

    fun deleteAccount() = viewModelScope.launch {
        //todo - make delete
    }

    fun chooseImage() = viewModelScope.launch {
        eventListener.emit(UserDataEvents.OpenImagePicker)
    }


    fun showDeleteAccountDialog() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(showDeleteAccountDialog = true)
        }
    }

    fun showLogoutDialog() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(showLogoutDialog = true)
        }
    }

    fun closeLogoutDialog() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(showLogoutDialog = false)
        }

    }

    fun closeDeleteAccountDialog() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(showDeleteAccountDialog = false)
        }
    }

    fun checkDatePicker(field: FieldUi) = viewModelScope.launch {
        if (field.id != "data") return@launch

        uiStateListener.updateData { s ->
            s.copy(showDatePicker = true)
        }
    }

    fun closeDatePicker() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(showDatePicker = false)
        }
    }

    fun changeDate(date: LocalDate) = viewModelScope.launch {
        val dateField = dataState.fields.firstOrNull { it.id == "data" } ?: return@launch
        uiStateListener.updateData { s ->
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val formattedDate = date.format(formatter)
            val updatedFields = s.fields.updateFieldAndResetErrors(
                dateField,
                dateField.copy(value = formattedDate)
            )

            s.copy(
                fields = updatedFields,
                showDatePicker = false,
                buttonEnabled = updatedFields.checkFields()
            )
        }
    }


    sealed class UserDataEvents : Event {
        data class UpdateUserDataEvent(val message: String) : UserDataEvents()
        data class NavigateToGenderChoose(val gender: String) : UserDataEvents()
        data class ShowSnackbar(val message: String) : UserDataEvents()

        data object ShowDatePicker : UserDataEvents()
        data object UpdateProfile : UserDataEvents()
        data object Logout : UserDataEvents()
        data object GoBack : UserDataEvents()
        data object OpenImagePicker : UserDataEvents()
    }

    sealed interface UserDataUiState {
        data object Loading : UserDataUiState
        data object Error : UserDataUiState
        data object Success : UserDataUiState
    }

    @Immutable
    data class UserDataState(
        val item: UserDataUI? = null,
        val canChangeBirthDay: Boolean = true,
        val showPassword: Boolean = false,

        val fields: List<FieldUi> = emptyList(),
        val title: String = "",
        val photo: String = "",
        val photoTitle: String = "",
        val photoDescription: String = "",
        val uiState: UserDataUiState = UserDataUiState.Loading,
        val buttonEnabled: Boolean = false,
        val showLogoutDialog: Boolean = false,
        val showDeleteAccountDialog: Boolean = false,
        val showDatePicker: Boolean = false,
    ) : State

}