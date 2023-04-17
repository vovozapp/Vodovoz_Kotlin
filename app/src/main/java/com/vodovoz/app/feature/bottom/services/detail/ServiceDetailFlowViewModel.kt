package com.vodovoz.app.feature.bottom.services.detail

import androidx.lifecycle.SavedStateHandle
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.feature.bottom.services.AboutServicesFlowViewModel
import com.vodovoz.app.ui.model.custom.AboutServicesBundleUI
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ServiceDetailFlowViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val repository: MainRepository
) : PagingContractViewModel<ServiceDetailFlowViewModel.ServiceDetailState, ServiceDetailFlowViewModel.ServiceDetailEvents>(
    ServiceDetailState()
) {

    private val typeList = savedState.get<Array<String>>("typeList")
    private val type = savedState.get<String>("selectedType")



    sealed class ServiceDetailEvents : Event {

    }

    data class ServiceDetailState(
        val item: AboutServicesBundleUI? = null
    ) : State
}