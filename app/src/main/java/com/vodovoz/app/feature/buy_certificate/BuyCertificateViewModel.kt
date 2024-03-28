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
import com.vodovoz.app.ui.model.custom.OrderingCompletedInfoBundleUI
import com.vodovoz.app.util.FieldValidationsSettings
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

    private var resultFlow: BuyCertificateState =
        BuyCertificateState()

    fun getBuyCertificateBundle() {
        viewModelScope.launch {
            uiStateListener.value = state.copy(
                loadingPage = true
            )
            flow {
                emit(repository.fetchBuyCertificateInfo())
            }
                .flowOn(Dispatchers.IO)
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
                                        payment = certificateBundle.payment.copy(
                                            payMethods = payMethods
                                        )
                                    )
                                ),
                                loadingPage = false,
                                error = null
                            )
                            resultFlow = state.data.copy()

                            certificateBundle.buyCertificatePropertyUIList.forEach {
                                result[it.code] = ""
                            }

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

    fun addResult(key: String, value: String) {
        result[key] = value
        if (key == "buyMoney") {
            result[key] = buildString {
                append("$value@")
                append(resultFlow.buyCertificateBundleUI
                    ?.buyCertificatePropertyUIList?.firstOrNull { it.code == key }
                    ?.buyCertificateFieldUIList?.firstOrNull { it.id == value }
                    ?.name
                )
            }
        }
        if (key != "quanity") {
            resultFlow = resultFlow.copy(
                buyCertificateBundleUI = resultFlow.buyCertificateBundleUI?.copy(
                    buyCertificatePropertyUIList = resultFlow.buyCertificateBundleUI?.buyCertificatePropertyUIList?.map { property ->
                        if (key == property.code) {
                            if (key == "buyMoney") {
                                property.copy(
                                    buyCertificateFieldUIList = property.buyCertificateFieldUIList?.map {
                                        if (value == it.id) {
                                            it.copy(
                                                isSelected = true
                                            )
                                        } else {
                                            it.copy(
                                                isSelected = false
                                            )
                                        }
                                    },
                                    error = false,
                                    currentValue = value
                                )
                            } else {
                                property.copy(error = false, currentValue = value)
                            }
                        } else {
                            property
                        }
                    } ?: return
                )
            )
        }
    }

//    fun changeCertificate(currentCert: BuyCertificateFieldUI) {
//        uiStateListener.value = state.copy(
//            data = state.data.copy(
//                buyCertificateBundleUI = state.data.buyCertificateBundleUI?.copy(
//                    buyCertificatePropertyUIList = state.data.buyCertificateBundleUI?.buyCertificatePropertyUIList?.map {
//                        if ("" == it.code) {
//                            it.copy(error = false, currentValue = value)
//                        } else {
//                            it
//                        }
//                    } ?: return
//                )
//            )
//        )
//    }


    fun setSelectedPaymentMethod(itemId: Long) {
        val oldList =
            state.data.buyCertificateBundleUI?.payment?.payMethods ?: return
        addResult(state.data.buyCertificateBundleUI?.payment?.code ?: "", itemId.toString())
        resultFlow = resultFlow.copy(
            buyCertificateBundleUI = resultFlow.buyCertificateBundleUI?.copy(
                payment = resultFlow.buyCertificateBundleUI?.payment?.copy(
                    payMethods = oldList.map {
                        it.copy(isSelected = itemId == it.id)
                    }
                ) ?: return
            )
        )
        uiStateListener.value = state.copy(
            data = resultFlow
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
            result.forEach { resultItem ->
                val property =
                    state.data.buyCertificateBundleUI?.buyCertificatePropertyUIList?.firstOrNull {
                        it.code == resultItem.key
                    }
                if (property != null) {
                    if (property.required && resultItem.value.isEmpty() ||
                        resultItem.key == "email" && !FieldValidationsSettings.EMAIL_REGEX.matches(
                            resultItem.value
                        )
                    ) {
                        resultFlow = resultFlow.copy(
                            buyCertificateBundleUI = resultFlow.buyCertificateBundleUI?.copy(
                                buyCertificatePropertyUIList = resultFlow.buyCertificateBundleUI?.buyCertificatePropertyUIList?.map {
                                    if (property.code == it.code) {
                                        it.copy(error = true)
                                    } else {
                                        it
                                    }
                                } ?: return@launch
                            )
                        )
                        uiStateListener.value = state.copy(
                            data = resultFlow
                        )
                        return@launch
                    }
                }
            }
            val paymentCode = state.data.buyCertificateBundleUI?.payment?.code ?: ""
            if (paymentCode.isNotEmpty()) {
                val res = result[paymentCode]
                if (res.isNullOrEmpty()) {
                    resultFlow = resultFlow.copy(
                        buyCertificateBundleUI = resultFlow.buyCertificateBundleUI?.copy(
                            payment = resultFlow.buyCertificateBundleUI?.payment?.copy(
                                error = true
                            ) ?: return@launch
                        )
                    )

                    uiStateListener.value = state.copy(
                        data = resultFlow
                    )
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
                .flowOn(Dispatchers.IO)
                .onEach { response ->
                    when (response) {
                        is ResponseEntity.Success -> {
                            val data = response.data.mapToUI()
                            uiStateListener.value = state.copy(
                                data = state.data.copy(
                                    orderingCompletedInfoBundleUI = data
                                ),
                                loadingPage = false,
                                error = null
                            )
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

    data class BuyCertificateState(
        val buyCertificateBundleUI: BuyCertificateBundleUI? = null,
        val orderingCompletedInfoBundleUI: OrderingCompletedInfoBundleUI? = null,
    ) : State

    sealed interface BuyCertificateEvents : Event {

        data class ShowPaymentMethod(
            val list: List<PayMethodUI>,
            val selectedPayMethodId: Long?,
        ) : BuyCertificateEvents

        data class OrderSuccess(
            val data: OrderingCompletedInfoBundleUI,
        ) : BuyCertificateEvents
    }
}