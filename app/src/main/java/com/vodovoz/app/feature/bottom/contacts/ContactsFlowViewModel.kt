package com.vodovoz.app.feature.bottom.contacts

import android.os.Build
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.*
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.contacts.ContactsBundleResponseJsonParser.parseContactsBundleResponse
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

    data class ContactsState(
        val item: ContactsBundleUI? = null
    ) : State

    sealed class ContactsEvents : Event {

    }
}