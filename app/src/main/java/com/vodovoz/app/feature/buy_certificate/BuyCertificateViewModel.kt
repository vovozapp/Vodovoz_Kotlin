package com.vodovoz.app.feature.buy_certificate

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.BuyCertificateBundleMapper.mapToUI
import com.vodovoz.app.mapper.OrderingCompletedInfoBundleMapper.mapToUI
import com.vodovoz.app.ui.model.PayMethodUI
import com.vodovoz.app.ui.model.custom.BuyCertificateBundleUI
import com.vodovoz.app.ui.model.custom.BuyCertificateTypeUI
import com.vodovoz.app.ui.model.custom.OrderingCompletedInfoBundleUI
import com.vodovoz.app.util.extensions.debugLog
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
) : PagingContractViewModel<BuyCertificateViewModel.BuyCertificateState, BuyCertificateViewModel.BuyCertificateEvents>(
    BuyCertificateState()
) {

    private val result: HashMap<String, String> = hashMapOf()

    init {
        getBuyCertificateBundle()
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

    fun buyCertificate() {
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
            UiAction.BuyCertificate -> buyCertificate()
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

    data class BuyCertificateState(
        val buyCertificateBundleUI: BuyCertificateBundleUI? = null,
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

        data class OpenLink(val url: String) : BuyCertificateEvents
    }

    sealed interface UiAction {

        data class OnSelectCertificate(val id: String): UiAction

        object OnIncreaseCount: UiAction

        object OnDecreaseCount: UiAction

        data class OnSelectType(val type: BuyCertificateTypeUI): UiAction

        data class AddResult(val code: String, val value: String): UiAction

        object ShowPaymentMethods: UiAction

        object BuyCertificate: UiAction

    }
}

typealias OnAction = (BuyCertificateViewModel.UiAction) -> Unit