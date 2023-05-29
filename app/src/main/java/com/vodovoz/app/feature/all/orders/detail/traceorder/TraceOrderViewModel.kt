package com.vodovoz.app.feature.all.orders.detail.traceorder

import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TraceOrderViewModel @Inject constructor() : PagingContractViewModel<TraceOrderViewModel.TraceOrderState, TraceOrderViewModel.TraceOrderEvents>(TraceOrderState()) {



    data class TraceOrderState(
        val item: Item? = null
    ) : State

    sealed class TraceOrderEvents : Event
}