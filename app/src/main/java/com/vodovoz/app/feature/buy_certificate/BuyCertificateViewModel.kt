package com.vodovoz.app.feature.buy_certificate

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.common.resources.ResourcesProvider
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.design_system.model.PaymentTypeUi
import com.vodovoz.app.design_system.model.VodovozPlaceholderUi
import com.vodovoz.app.design_system.model.mapToUi
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.domain.general.model.toQueries
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.buy_certificate.model.BuyCertificateCodesUi
import com.vodovoz.app.feature.buy_certificate.model.BuyCertificateErrorsUi
import com.vodovoz.app.feature.buy_certificate.model.BuyCertificateTabUi
import com.vodovoz.app.feature.buy_certificate.model.CertificateUi
import com.vodovoz.app.feature.buy_certificate.model.FAQUi
import com.vodovoz.app.feature.buy_certificate.model.PaymentInfoUi
import com.vodovoz.app.feature.buy_certificate.model.mapToUi
import com.vodovoz.app.feature.buy_certificate.model.toUi
import com.vodovoz.app.feature.preorder.model.FieldUi
import com.vodovoz.app.feature.preorder.model.checkFields
import com.vodovoz.app.feature.preorder.model.getErrorText
import com.vodovoz.app.feature.preorder.model.mapToDomain
import com.vodovoz.app.feature.preorder.model.updateFieldAndResetErrors
import com.vodovoz.app.feature.preorder.model.vodovozValidators
import com.vodovoz.app.mapper.BuyCertificateBundleMapper.mapToUI
import com.vodovoz.app.mapper.OrderingCompletedInfoBundleMapper.mapToUI
import com.vodovoz.app.ui.model.PayMethodUI
import com.vodovoz.app.ui.model.custom.BuyCertificateBundleUI
import com.vodovoz.app.ui.model.custom.BuyCertificateTypeUI
import com.vodovoz.app.ui.model.custom.OrderingCompletedInfoBundleUI
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
import javax.inject.Inject

@HiltViewModel
class BuyCertificateViewModel @Inject constructor(
    private val repository: MainRepository,
    private val accountManager: AccountManager,
    private val vodovozServiceRepository: VodovozServiceRepository,
    private val resourcesProvider: ResourcesProvider,
) : PagingContractViewModel<BuyCertificateViewModel.BuyCertificateState, BuyCertificateViewModel.BuyCertificateEvents>(
    BuyCertificateState()
) {

    private val result: HashMap<String, String> = hashMapOf()

    init {
        getBuyCertificateBundle()
    }

    fun fetchBuyCertificateDetails() = viewModelScope.launch {
        if (dataState.uiState is BuyCertificateUiState.Success) return@launch

        uiStateListener.updateData { s ->
            s.copy(uiState = BuyCertificateUiState.Loading)
        }

        val buyCertificateDetailsResult =
            vodovozServiceRepository.getBuyCertificateDetails().singleResult()

        buyCertificateDetailsResult.onSuccess { buyCertificateDetails ->
            uiStateListener.updateData { s ->

                val tabs = buyCertificateDetails.tabs.mapToUi()
                val paymentTypes = buyCertificateDetails.paymentTypes.mapToUi()

                s.copy(
                    uiState = BuyCertificateUiState.Body,
                    title = buyCertificateDetails.title,
                    certificates = buyCertificateDetails.certificates.mapToUi(),
                    certificatesTitle = buyCertificateDetails.certificatesTitle,
                    tabs = tabs,
                    button = buyCertificateDetails.button.toUi(),
                    paymentTitle = buyCertificateDetails.paymentTitle,
                    faq = buyCertificateDetails.faq.toUi(),
                    paymentTypes = paymentTypes,
                    codes = buyCertificateDetails.codes.toUi(),
                    currentTab = tabs.firstOrNull() ?: s.currentTab,
                    currentPaymentType = paymentTypes.firstOrNull() ?: s.currentPaymentType
                )
            }
        }.onFailure {
            uiStateListener.updateData { s ->
                s.copy(uiState = BuyCertificateUiState.Error)
            }
        }
    }

    private fun getBuyCertificateBundle() {
        viewModelScope.launch {
            uiStateListener.value = state.copy(
                loadingPage = true
            )
            val userId = accountManager.fetchAccountId()
            if (userId == null) {
                eventListener.emit(BuyCertificateEvents.AuthError)
                return@launch
            }
            flow {
                emit(repository.fetchBuyCertificateInfo(userId))
            }
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        response.data.mapToUI().let { certificateBundle ->
                            val payMethods = if (certificateBundle.payment.payMethods.size == 1) {
                                result[certificateBundle.payment.code] =
                                    certificateBundle.payment.payMethods[0].id.toString()
                                certificateBundle.payment.payMethods.map {
                                    it.copy(isSelected = true)
                                }
                            } else {
                                certificateBundle.payment.payMethods
                            }

                            uiStateListener.value = state.copy(
                                data = state.data.copy(
                                    buyCertificateBundleUI = certificateBundle.copy(
                                        typeList = certificateBundle.typeList?.mapIndexed { index, type ->
                                            type.copy(
                                                isSelected = index == 0,
                                                buyCertificatePropertyList = type.buyCertificatePropertyList?.map { property ->
                                                    if (property.code.contains("email")) {
                                                        property.copy(
                                                            currentValue = property.value,
                                                        )
                                                    } else {
                                                        property.copy()
                                                    }
                                                }
                                            )
                                        },
                                        payment = certificateBundle.payment.copy(
                                            payMethods = payMethods
                                        )
                                    )
                                ),
                                loadingPage = false,
                                error = null
                            )
                        }
                    } else {
                        uiStateListener.value =
                            state.copy(
                                loadingPage = false,
                                error = ErrorState.Error()
                            )
                    }
                }.catch {
                    debugLog { "fetch certificate bundle error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun selectCertificate(certificate: CertificateUi) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                currentCertificate = certificate,
                errors = s.errors.copy(certificate = false)
            )
        }
    }


    fun selectCertificate(id: String) {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                buyCertificateBundleUI = state.data.buyCertificateBundleUI?.copy(
                    certificateInfo = state.data.buyCertificateBundleUI?.certificateInfo?.copy(
                        buyCertificateFieldUIList = state.data.buyCertificateBundleUI?.certificateInfo?.buyCertificateFieldUIList?.map {
                            if (id == it.id) {
                                it.copy(isSelected = true)
                            } else {
                                it.copy(isSelected = false)
                            }
                        },
                        error = false
                    )
                )
            )
        )
    }

    fun addResult(key: String, value: String) {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                buyCertificateBundleUI = state.data.buyCertificateBundleUI?.copy(
                    typeList = state.data.buyCertificateBundleUI?.typeList?.map { type ->
                        type.copy(
                            buyCertificatePropertyList = type.buyCertificatePropertyList?.map { property ->
                                if (key == property.code) {
                                    property.copy(error = false, currentValue = value)
                                } else {
                                    property
                                }
                            } ?: return
                        )
                    }
                )
            )
        )
    }

    fun setSelectedPaymentMethod(itemId: Long) {
        val oldList =
            state.data.buyCertificateBundleUI?.payment?.payMethods ?: return
        uiStateListener.value = state.copy(
            data = state.data.copy(
                buyCertificateBundleUI = state.data.buyCertificateBundleUI?.copy(
                    payment = state.data.buyCertificateBundleUI?.payment?.copy(
                        payMethods = oldList.map {
                            it.copy(isSelected = itemId == it.id)
                        }
                    ) ?: return
                )
            )
        )
    }

    fun selectType(selectedType: BuyCertificateTypeUI) {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                buyCertificateBundleUI = state.data.buyCertificateBundleUI?.copy(
                    typeList = state.data.buyCertificateBundleUI?.typeList?.map { type ->
                        type.copy(
                            isSelected = selectedType.type == type.type
                        )
                    }
                )
            )
        )
    }

    fun showPaymentMethods() {
        viewModelScope.launch {
            eventListener.emit(
                BuyCertificateEvents.ShowPaymentMethod(
                    state.data.buyCertificateBundleUI?.payment?.payMethods ?: emptyList(),
                    state.data.buyCertificateBundleUI?.payment?.payMethods?.firstOrNull { it.isSelected }?.id
                )
            )
        }
    }

    @Deprecated("Use buyCertificate")
    fun buyCertificateOld() {
        viewModelScope.launch {
            result.clear()
            state.data.buyCertificateBundleUI?.let { bundle ->
                result[bundle.certificateInfo?.code ?: "buyMoney"] = ""
                bundle.certificateInfo?.buyCertificateFieldUIList?.firstOrNull { it.isSelected }
                    ?.let {
                        result[bundle.certificateInfo.code] = buildString {
                            append("${it.id}@")
                            append(it.name)
                        }
                    }

                if (bundle.certificateInfo?.required == true
                    && result[bundle.certificateInfo.code].isNullOrEmpty()
                ) {
                    uiStateListener.value = state.copy(
                        data = state.data.copy(
                            buyCertificateBundleUI = state.data.buyCertificateBundleUI?.copy(
                                certificateInfo = state.data.buyCertificateBundleUI?.certificateInfo?.copy(
                                    error = true
                                )
                            )
                        )
                    )
                    return@launch
                }

                if (bundle.certificateInfo?.showAmount == true) {
                    result["QUANITY"] = bundle.certificateInfo.count.toString()
                }

                bundle.typeList
                    ?.firstOrNull { it.isSelected }?.let { type ->
                        result[type.code] = type.type.toString()
                        type.buyCertificatePropertyList?.forEach { property ->
                            if (property.required && property.currentValue.isEmpty()) {
                                uiStateListener.value = state.copy(
                                    data = state.data.copy(
                                        buyCertificateBundleUI = state.data.buyCertificateBundleUI?.copy(
                                            typeList = state.data.buyCertificateBundleUI?.typeList?.map { type ->
                                                if (type.isSelected) {
                                                    type.copy(
                                                        buyCertificatePropertyList = type.buyCertificatePropertyList?.map {
                                                            if (it.code == property.code) {
                                                                it.copy(error = true)
                                                            } else {
                                                                it
                                                            }
                                                        }
                                                    )
                                                } else {
                                                    type.copy()
                                                }
                                            }
                                        )
                                    )
                                )
                            }
                            result[property.code] = property.currentValue
                        }
                    }

                result[bundle.payment.code] =
                    bundle.payment.payMethods.firstOrNull { it.isSelected }?.id?.toString() ?: ""
                if (result[bundle.payment.code].isNullOrEmpty()) {
                    uiStateListener.value = state.copy(
                        data = state.data.copy(
                            buyCertificateBundleUI = state.data.buyCertificateBundleUI?.copy(
                                payment = state.data.buyCertificateBundleUI?.payment?.copy(
                                    error = true
                                ) ?: return@launch
                            )
                        )
                    )
                    return@launch
                }
            }

            val userId = accountManager.fetchAccountId() ?: return@launch

            flow {
                emit(
                    repository.buyCertificate(
                        userId = userId,
                        buyCertificateMap = result
                    )
                )
            }
                .onEach { response ->
                    when (response) {
                        is ResponseEntity.Success -> {
                            val data = response.data.mapToUI()
                            eventListener.emit(BuyCertificateEvents.OrderSuccess(data))
                        }

                        is ResponseEntity.Error -> {
                            uiStateListener.value =
                                state.copy(
                                    loadingPage = false,
                                    error = ErrorState.Error(response.errorMessage)
                                )
                        }

                        else -> {}
                    }
                }
                .flowOn(Dispatchers.Default)
                .catch {
                    debugLog { "buy cert error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun increaseCount() {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                buyCertificateBundleUI = state.data.buyCertificateBundleUI?.copy(
                    certificateInfo = state.data.buyCertificateBundleUI?.certificateInfo?.copy(
                        count = state.data.buyCertificateBundleUI?.certificateInfo?.count?.plus(1)
                            ?: 1
                    )
                )
            )
        )
    }

    fun decreaseCount() {
        val oldCount = state.data.buyCertificateBundleUI?.certificateInfo?.count ?: 1
        if (oldCount > 1) {
            uiStateListener.value = state.copy(
                data = state.data.copy(
                    buyCertificateBundleUI = state.data.buyCertificateBundleUI?.copy(
                        certificateInfo = state.data.buyCertificateBundleUI?.certificateInfo?.copy(
                            count = oldCount.minus(1)
                        )
                    )
                )
            )
        }
    }

    fun onAction(action: UiAction) {
        when (action) {
            is UiAction.AddResult -> addResult(action.code, action.value)
            UiAction.BuyCertificate -> buyCertificateOld()
            UiAction.OnDecreaseCount -> decreaseCount()
            UiAction.OnIncreaseCount -> increaseCount()
            is UiAction.OnSelectCertificate -> selectCertificate(action.id)
            is UiAction.OnSelectType -> selectType(action.type)
            UiAction.ShowPaymentMethods -> showPaymentMethods()
        }
    }

    fun openLink(url: String) {
        viewModelScope.launch {
            eventListener.emit(BuyCertificateEvents.OpenLink(url))
        }
    }

    fun navigateBack() = viewModelScope.launch {
        eventListener.emit(BuyCertificateEvents.GoBack)
    }

    fun selectTab(tab: BuyCertificateTabUi) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(currentTab = tab)
        }
    }

    fun changeField(field: FieldUi, updatedField: FieldUi) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                currentTab = s.currentTab.copy(
                    fields = s.currentTab.fields.updateFieldAndResetErrors(field, updatedField)
                )
            )
        }
    }

    fun selectPaymentType(paymentType: PaymentTypeUi) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                currentPaymentType = paymentType,
                errors = s.errors.copy(payment = false)
            )
        }
    }

    fun activateButton(button: ColorfulButtonUi) = viewModelScope.launch {
        when (button.id) {
            "oformlenie" -> {
                buyCertificate()
            }

            "auth" -> {
                eventListener.emit(BuyCertificateEvents.GoToProfile)
            }
        }
    }

    private fun buyCertificate() = viewModelScope.launch {
        val currentTab = dataState.currentTab

        currentTab.fields.checkFields(
            putErrors = true,
            getSupportingText = { field ->
                field.getErrorText { id ->
                    resourcesProvider.getString(id)
                }
            },
            validators = vodovozValidators
        ) { fields, isValid ->
            val certificateError = dataState.currentCertificate == CertificateUi.Empty
            val paymentError = dataState.currentPaymentType == PaymentTypeUi.Empty

            uiStateListener.updateData { s ->
                s.copy(
                    errors = s.errors.copy(
                        certificate = certificateError,
                        payment = paymentError
                    ),
                    currentTab = currentTab.copy(
                        fields = fields
                    ),
                )
            }

            if (!isValid || certificateError || paymentError) return@launch
        }

        uiStateListener.updateData { s ->
            s.copy(button = s.button.copy(loading = true))
        }

        val certificate = dataState.currentCertificate
        val tab = dataState.currentTab
        val fields = tab.fields
        val codes = dataState.codes
        val paymentType = dataState.currentPaymentType


        val buyCertificateResult = vodovozServiceRepository.buyCertificate(
            mapOf(
                codes.certificates to certificate.id.toString(),
                codes.payment to paymentType.id.toString(),
                codes.tabs to tab.id.toString()
            ) + fields.mapToDomain().toQueries()
        ).singleResult()

        buyCertificateResult.onSuccess { buyCertificate ->
            val paymentInfo = buyCertificate.payment.toUi()
            uiStateListener.updateData { s ->
                s.copy(
                    uiState = BuyCertificateUiState.Success(buyCertificate.placeholder.toUi()),
                    paymentInfo = paymentInfo
                )
            }
        }.onFailure {
            eventListener.emit(BuyCertificateEvents.ShowToast(resourcesProvider.getString(R.string.order_failed)))
        }


    }

    fun pay() = viewModelScope.launch {
        val paymentInfo = dataState.paymentInfo ?: return@launch
        if (paymentInfo.browser) {
            eventListener.emit(BuyCertificateEvents.OpenUrl(paymentInfo.url))
        } else {
            eventListener.emit(BuyCertificateEvents.GoToWebView(paymentInfo.url))
        }

    }

    fun navigateToFAQ(faqUi: FAQUi) = viewModelScope.launch {
        eventListener.emit(BuyCertificateEvents.GoToFAQ(faqUi))
    }

    @Immutable
    data class BuyCertificateState(
        val buyCertificateBundleUI: BuyCertificateBundleUI? = null,

        val title: String = "",
        val uiState: BuyCertificateUiState = BuyCertificateUiState.Loading,
        val codes: BuyCertificateCodesUi = BuyCertificateCodesUi.Empty,
        val certificatesTitle: String = "",
        val certificates: List<CertificateUi> = emptyList(),
        val currentCertificate: CertificateUi = CertificateUi.Empty,
        val tabs: List<BuyCertificateTabUi> = emptyList(),
        val currentTab: BuyCertificateTabUi = BuyCertificateTabUi.Empty,
        val button: ColorfulButtonUi = ColorfulButtonUi.Empty,
        val paymentTitle: String = "",
        val paymentTypes: List<PaymentTypeUi> = emptyList(),
        val currentPaymentType: PaymentTypeUi = PaymentTypeUi.Empty,
        val errors: BuyCertificateErrorsUi = BuyCertificateErrorsUi.Empty,
        val faq: FAQUi = FAQUi.Empty,
        val paymentInfo: PaymentInfoUi? = null,
    ) : State

    sealed interface BuyCertificateEvents : Event {

        data class ShowPaymentMethod(
            val list: List<PayMethodUI>,
            val selectedPayMethodId: Long?,
        ) : BuyCertificateEvents

        data class OrderSuccess(
            val data: OrderingCompletedInfoBundleUI,
        ) : BuyCertificateEvents

        data object AuthError : BuyCertificateEvents
        data object GoBack : BuyCertificateEvents
        data object GoToProfile : BuyCertificateEvents

        data class OpenLink(val url: String) : BuyCertificateEvents
        data class GoToFAQ(val faq: FAQUi) : BuyCertificateEvents
        data class GoToWebView(val url: String) : BuyCertificateEvents
        data class OpenUrl(val url: String) : BuyCertificateEvents
        data class ShowToast(val message: String) : BuyCertificateEvents

    }

    sealed interface UiAction {

        data class OnSelectCertificate(val id: String) : UiAction

        object OnIncreaseCount : UiAction

        object OnDecreaseCount : UiAction

        data class OnSelectType(val type: BuyCertificateTypeUI) : UiAction

        data class AddResult(val code: String, val value: String) : UiAction

        object ShowPaymentMethods : UiAction

        object BuyCertificate : UiAction

    }

    @Immutable
    sealed interface BuyCertificateUiState {
        data object Loading : BuyCertificateUiState
        data object Error : BuyCertificateUiState
        data object Body : BuyCertificateUiState
        data class Success(val placeholder: VodovozPlaceholderUi) : BuyCertificateUiState
    }
}

typealias OnAction = (BuyCertificateViewModel.UiAction) -> Unit
