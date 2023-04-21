package com.vodovoz.app.feature.cart.ordering

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.config.ShippingAlertConfig
import com.vodovoz.app.ui.model.ShippingAlertUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderingFlowViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: MainRepository
) : PagingContractViewModel<OrderingFlowViewModel.OrderingState, OrderingFlowViewModel.OrderingEvents>(
    OrderingState()
) {

    fun fetchFreeShippingDaysInfo() {
        viewModelScope.launch {
            flow { emit(repository.fetchFreeShippingDaysInfoResponse()) }
        }
    }


    data class OrderingState(
        val item: Item? = null,
        val shippingAlertList: List<ShippingAlertUI> = ShippingAlertConfig.shippingAlertEntityListUI
    ) : State

    sealed class OrderingEvents : Event {

    }
}