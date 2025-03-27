package com.vodovoz.app.feature.certificate_activation

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.certificate_activation.model.CertificateActivationEvent
import com.vodovoz.app.feature.certificate_activation.model.CertificateActivationState
import com.vodovoz.app.feature.certificate_activation.model.CertificateActivationUiState
import com.vodovoz.app.feature.preorder.model.FieldUi
import com.vodovoz.app.feature.preorder.model.FieldValidationResult
import com.vodovoz.app.feature.preorder.model.KeyboardTypeValidator
import com.vodovoz.app.feature.preorder.model.toUi
import com.vodovoz.app.ui.mvi.MviViewModel
import com.vodovoz.app.util.extensions.singleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CertificateActivationViewModel @Inject constructor(
    private val vodovozServiceRepository: VodovozServiceRepository,
) : MviViewModel<CertificateActivationState, CertificateActivationEvent>(CertificateActivationState()) {


    init {
        fetchCertificateActivationDetails()
    }

    fun fetchCertificateActivationDetails() = viewModelScope.launch {
        _state.update { s ->
            s.copy(uiState = CertificateActivationUiState.Loading)
        }

        val certificateActivationDetailsResult =
            vodovozServiceRepository.getCertificateActivationDetails().singleResult()

        certificateActivationDetailsResult.onSuccess { certificateActivationDetails ->
            _state.update { s ->
                s.copy(
                    uiState = CertificateActivationUiState.Details,
                    title = certificateActivationDetails.title,
                    field = certificateActivationDetails.field.toUi(),
                    activationButton = certificateActivationDetails.button.toUi(),
                    descriptionHtml = certificateActivationDetails.textHtml,
                    secondDescriptionHtml = certificateActivationDetails.textUnderButtonHtml
                )
            }
        }.onFailure {

            _state.update { s ->
                s.copy(
                    uiState = CertificateActivationUiState.Details,
                )
            }
        }

    }

    fun navigateBack() = viewModelScope.launch {
        _events.emit(CertificateActivationEvent.GoBack)
    }

    fun activateCertificate() = viewModelScope.launch {
        _state.update { s -> s.copy(activationButtonIsLoading = true) }
        val activateCertificateResult = vodovozServiceRepository.activateCertificate(stateSnapshot.field).singleResult()

        activateCertificateResult.onSuccess { message ->
            _state.update { s ->
                s.copy(
                    uiState = CertificateActivationUiState.CertificateActivated(message),
                    activationButtonIsLoading = false
                )
            }
        }.onFailure { fail ->
            _state.update { s ->
                s.copy(
                    field = s.field.copy(
                        isError = true,
                        supportingText = fail.message ?: ""
                    ),
                    activationButtonIsLoading = false
                )
            }
        }
    }

    fun changeFieldValue(field: FieldUi, newValue: String) = viewModelScope.launch {
        val validators = listOf(KeyboardTypeValidator)
        val updatedField = field.copy(value = newValue, isError = false, supportingText = "")

        _state.update { s ->
            s.copy(
                field = updatedField,
                activationButtonEnabled = newValue.isNotBlank() && validators.any { fieldValidator ->
                    fieldValidator.isValid(updatedField) != FieldValidationResult.INVALID
                }
            )
        }
    }


}