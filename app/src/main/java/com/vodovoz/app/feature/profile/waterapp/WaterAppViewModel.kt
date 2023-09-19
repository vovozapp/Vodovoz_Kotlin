package com.vodovoz.app.feature.profile.waterapp

import com.squareup.moshi.Moshi
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WaterAppViewModel @Inject constructor(
    private val waterAppHelper: WaterAppHelper,
    private val moshi: Moshi
) : PagingContractViewModel<WaterAppViewModel.WaterAppState, WaterAppViewModel.WaterAppEvents>(
    WaterAppState()
) {

    data class WaterAppState(
        val userData: WaterAppHelper.WaterAppUserData = WaterAppHelper.WaterAppUserData()
    ): State

    sealed class WaterAppEvents : Event


}