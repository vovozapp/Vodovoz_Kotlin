package com.vodovoz.app.feature.auth.reg

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.resources.ResourcesProvider
import com.vodovoz.app.common.token.FirebaseTokenManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.config.AuthConfig
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.domain.general.model.ValidationException
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.preorder.model.FieldUi
import com.vodovoz.app.feature.preorder.model.checkFields
import com.vodovoz.app.feature.preorder.model.toDomain
import com.vodovoz.app.feature.preorder.model.toUi
import com.vodovoz.app.feature.preorder.model.updateFieldValueAndResetErrors
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.singleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    private val vodovozServiceRepository: VodovozServiceRepository,
    private val resourceProvider: ResourcesProvider,
) : PagingContractViewModel<RegFlowViewModel.RegState, RegFlowViewModel.RegEvents>(RegState()) {

    init {
        fetchRegisterFields()
    }

    fun fetchRegisterFields() = viewModelScope.launch {
        uiStateListener.updateData { s -> s.copy(uiState = UiState.Loading) }

        val registerFieldsResult =
            vodovozServiceRepository.getRegisterFields().singleResult()

        registerFieldsResult.onSuccess { fieldsSection ->
            uiStateListener.updateData { s ->
                s.copy(
                    uiState = UiState.Success,
                    fields = fieldsSection.items.map { fieldModel -> fieldModel.toUi() },
                    title = fieldsSection.title
                )
            }
        }.onFailure {
            uiStateListener.updateData { s -> s.copy(uiState = UiState.Error) }
        }
    }

    fun register() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(buttonLoading = true)
        }

        val failMessage = resourceProvider.getString(R.string.registration_fail)

        val registerResult = vodovozServiceRepository.register(
            dataState.fields.map { fieldUi -> fieldUi.toDomain() }
        ).singleResult()

        registerResult.onSuccess { userId ->
            accountManager.updateUserId(userId)
            likeManager.updateLikesAfterLogin(userId)
            firebaseTokenManager.sendFirebaseToken()
            eventListener.emit(RegEvents.RegSuccess)
            //Todo - save last login data
//            accountManager.updateLastLoginSetting(
//                AccountManager.UserSettings(
//                    email,
//                    password
//                )
//            )

            uiStateListener.updateData { s ->
                s.copy(
                    buttonEnabled = false,
                    buttonLoading = false,
                    uiState = UiState.Success
                )
            }

            eventListener.emit(RegEvents.GoToProfile)


        }.onFailure { t ->
            val message = when(t){
                is ValidationException -> t.message ?: failMessage
                else -> failMessage
            }
            eventListener.emit(RegEvents.ShowSnackbar(message))
            uiStateListener.updateData { s ->
                s.copy(
                    buttonEnabled = false,
                    buttonLoading = false,
                    uiState = UiState.Success
                )
            }
        }


    }.invokeOnCompletion { uiStateListener.updateData { s -> s.copy(buttonLoading = false) } }

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

    fun navigateBack() = viewModelScope.launch {
        eventListener.emit(RegEvents.GoBack)
    }

    fun changeFieldValue(field: FieldUi, newValue: String) = viewModelScope.launch {
        val updatedFields = dataState.fields.updateFieldValueAndResetErrors(field, newValue)

        updatedFields.checkFields { fields, isValid ->
            uiStateListener.updateData { s ->
                s.copy(
                    fields = fields,
                    buttonEnabled = isValid
                )
            }
        }
    }

    fun changeFieldVisibility(field: FieldUi) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            val fields = s.fields
            val fieldIndex = fields.indexOfFirst { field.id == it.id }

            s.copy(
                fields = fields.toMutableList()
                    .apply {
                        set(
                            index = fieldIndex,
                            element = field.copy(isValueVisible = !field.isValueVisible)
                        )
                    }
                    .map { fieldUi -> fieldUi.copy(isError = false) }
            )
        }
    }


    sealed class RegEvents : Event {
        data object RegSuccess : RegEvents()
        data class RegError(val message: String) : RegEvents()
        data class ShowSnackbar(val message: String) : RegEvents()
        data object GoBack : RegEvents()
        data object GoToProfile : RegEvents()
    }

    @Immutable
    data class RegState(
        val items: List<Item> = emptyList(),
        val fields: List<FieldUi> = emptyList(),
        val uiState: UiState = UiState.Loading,
        val title: String = "",
        val buttonEnabled: Boolean = false,
        val buttonLoading: Boolean = false,
    ) : State

    sealed interface UiState {
        data object Error : UiState
        data object Loading : UiState
        data object Success : UiState
    }
}