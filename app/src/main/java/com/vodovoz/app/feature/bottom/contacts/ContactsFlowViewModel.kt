package com.vodovoz.app.feature.bottom.contacts

import android.os.Build
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.*
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.contacts.ContactsBundleResponseJsonParser.parseContactsBundleResponse
import com.vodovoz.app.data.parser.response.contacts.SendMailResponseJsonParser.parseSendMailResponse
import com.vodovoz.app.data.parser.response.service.AboutServicesResponseJsonParser.parseAboutServicesResponse
import com.vodovoz.app.mapper.AboutServicesBundleMapper.mapToUI
import com.vodovoz.app.mapper.ContactsBundleMapper.mapToUI
import com.vodovoz.app.ui.model.ContactsBundleUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsFlowViewModel @Inject constructor(
    private val repository: MainRepository
) : PagingContractViewModel<ContactsFlowViewModel.ContactsState, ContactsFlowViewModel.ContactsEvents>(
    ContactsState()
) {

    fun fetchContacts() {
        viewModelScope.launch {
            flow { emit(repository.fetchContacts(Build.VERSION.RELEASE)) }
                .catch {
                    debugLog { "fetch contacts error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseContactsBundleResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()

                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                item = data
                            ),
                            loadingPage = false,
                            error = null
                        )

                    } else {
                        uiStateListener.value =
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

    fun sendMail(
        name: String,
        phone: String,
        email: String,
        descriptions: String
    ) {
        uiStateListener.value = state.copy(loadingPage = true)

        viewModelScope.launch {
            flow {
                emit(
                    repository.sendMail(
                        name = name,
                        phone = phone,
                        email = email,
                        comment = descriptions
                    )
                )
            }
                .catch {
                    debugLog { "send mail error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseSendMailResponse()
                    uiStateListener.value = state.copy(
                        loadingPage = false,
                        error = null
                    )
                    when (response) {
                        is ResponseEntity.Success -> eventListener.emit(
                            ContactsEvents.SendEmailSuccess(
                                "Ваще обращение успешно отправлено"
                            )
                        )
                        is ResponseEntity.Error -> eventListener.emit(
                            ContactsEvents.SendEmailError(
                                response.errorMessage
                            )
                        )
                        is ResponseEntity.Hide -> eventListener.emit(ContactsEvents.SendEmailError("Неизвестная ошибка"))
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    data class ContactsState(
        val item: ContactsBundleUI? = null
    ) : State

    sealed class ContactsEvents : Event {
        data class SendEmailSuccess(val message: String) : ContactsEvents()
        data class SendEmailError(val message: String) : ContactsEvents()
    }
}