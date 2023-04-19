package com.vodovoz.app.feature.addresses.add

import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.data.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddAddressFlowViewModel @Inject constructor(
    private val repository: MainRepository
) : PagingContractViewModel<AddAddressFlowViewModel.AddAddressState, AddAddressFlowViewModel.AddAddressEvents>(AddAddressState()){



    sealed class AddAddressEvents : Event {

    }

    data class AddAddressState(
        val item: Item? = null
    ) : State
}