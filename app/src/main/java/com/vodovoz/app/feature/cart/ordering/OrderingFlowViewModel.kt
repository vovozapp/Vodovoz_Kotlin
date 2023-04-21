package com.vodovoz.app.feature.cart.ordering

import androidx.lifecycle.SavedStateHandle
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.config.ShippingAlertConfig
import com.vodovoz.app.data.model.common.ShippingAlertEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrderingFlowViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: MainRepository
) : PagingContractViewModel<OrderingFlowViewModel.OrderingState, OrderingFlowViewModel.OrderingEvents>(
    OrderingState()
) {



    data class OrderingState(
        val item: Item? = null,
        val shippingAlertList: List<ShippingAlertEntity> = ShippingAlertConfig.shippingAlertEntityList
    ) : State

    sealed class OrderingEvents : Event {

    }
}