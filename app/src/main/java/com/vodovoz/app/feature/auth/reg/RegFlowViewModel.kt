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
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.preorder.model.FieldUi
import com.vodovoz.app.feature.preorder.model.KeyboardTypeValidator
import com.vodovoz.app.feature.preorder.model.NameValidator
import com.vodovoz.app.feature.preorder.model.toDomain
import com.vodovoz.app.feature.preorder.model.toUi
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.singleOrNull
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
        fetchFields()
    }

    fun fetchFields() = viewModelScope.launch {
        uiStateListener.updateData { s -> s.copy(uiState = UiState.Loading) }

        val registerFieldsResult =
            vodovozServiceRepository.getRegisterFields().singleOrNull() ?: run {
                uiStateListener.updateData { s -> s.copy(uiState = UiState.Error) }
                return@launch
            }

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
        if(!checkFields(true)) return@launch
        uiStateListener.updateData { s -> s.copy(buttonLoading = true) }

        val failMessage = resourceProvider.getString(R.string.registration_fail)

        val registerResult = vodovozServiceRepository.register(
            dataState.fields.map { fieldUi -> fieldUi.toDomain() }
        ).singleOrNull() ?: run {
            eventListener.emit(RegEvents.ShowSnackbar(failMessage))
            return@launch
        }

        registerResult.onSuccess { userId ->
            accountManager.updateUserId(userId)
            likeManager.updateLikesAfterLogin(userId)
//            accountManager.updateLastLoginSetting(
//                AccountManager.UserSettings(
//                    email,
//                    password
//                )
//            )

            firebaseTokenManager.sendFirebaseToken()
            eventListener.emit(RegEvents.RegSuccess)
        }.onFailure { t ->
            eventListener.emit(RegEvents.ShowSnackbar(t.message ?: failMessage))
        }


    }.invokeOnCompletion { uiStateListener.updateData { s -> s.copy(buttonLoading = false) } }

    private fun checkFields(hasErrors: Boolean = false): Boolean {
        var isValidFields = true
        val validators = listOf(NameValidator, KeyboardTypeValidator)

        val newFields = dataState.fields.map { field ->
            val isCorrect = validators.all { fieldValidator -> fieldValidator.validate(field) }
            if (!isCorrect) {
                isValidFields = false
                if (hasErrors) return@map field.copy(isError = true)
            }
            field
        }

        uiStateListener.updateData { s ->
            s.copy(
                buttonEnabled = isValidFields,
                fields = newFields
            )
        }

        return isValidFields
    }

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
        uiStateListener.updateData { s ->
            val fields = s.fields
            val fieldIndex = fields.indexOfFirst { field.id == it.id }

            s.copy(
                fields = fields.toMutableList()
                    .apply { set(index = fieldIndex, element = field.copy(value = newValue)) }
                    .map { field -> field.copy(isError = false) }
            )
        }

        checkFields()
    }

    fun changeFieldVisibility(field: FieldUi) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            val fields = s.fields
            val fieldIndex = fields.indexOfFirst { field.id == it.id }

            s.copy(
                fields = fields.toMutableList()
                    .apply { set(index = fieldIndex, element = field.copy(isValueVisible = !field.isValueVisible)) }
                    .map { fieldUi -> fieldUi.copy(isError = false) }
            )
        }
    }


    sealed class RegEvents : Event {
        data object RegSuccess : RegEvents()
        data class RegError(val message: String) : RegEvents()
        data class ShowSnackbar(val message: String) : RegEvents()
        data object GoBack : RegEvents()
    }

    @Immutable
    data class RegState(
        val items: List<Item> = emptyList(),
        val fields: List<FieldUi> = emptyList(),
        val uiState: UiState = UiState.Loading,
        val title: String = "",
        val buttonEnabled: Boolean = false,
        val buttonLoading: Boolean = false
    ) : State

    sealed interface UiState {
        data object Error : UiState
        data object Loading : UiState
        data object Success : UiState
    }
}