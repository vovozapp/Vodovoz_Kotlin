package com.vodovoz.app.feature.profile.waterapp

import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaterAppViewModel @Inject constructor(
    private val waterAppHelper: WaterAppHelper,
    private val moshi: Moshi
) : PagingContractViewModel<WaterAppViewModel.WaterAppState, WaterAppViewModel.WaterAppEvents>(
    WaterAppState()
) {

    init {

        waterAppHelper.fetchWaterAppUserData()

        viewModelScope.launch {
            waterAppHelper
                .observeWaterAppUserData()
                .collect {
                    uiStateListener.value = state.copy(
                        data = state.data.copy(
                            userData = it
                        )
                    )
                }
        }
    }

    fun saveGender(gender: String) {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                userData = state.data.userData.copy(
                    gender = gender
                )
            )
        )
    }

    fun saveHeight(height: String) {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                userData = state.data.userData.copy(
                    height = height
                )
            )
        )
    }

    fun saveWeight(weight: String) {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                userData = state.data.userData.copy(
                    weight = weight
                )
            )
        )
    }

    fun saveSleepTime(sleepTime: String) {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                userData = state.data.userData.copy(
                    sleepTime = sleepTime
                )
            )
        )
    }

    fun saveWakeUpTime(wakeUpTime: String) {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                userData = state.data.userData.copy(
                    wakeUpTime = wakeUpTime
                )
            )
        )
    }

    fun saveSport(sport: String) {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                userData = state.data.userData.copy(
                    sport = sport
                )
            )
        )
    }

    fun saveWaterAppUserData() {
        waterAppHelper.saveWaterAppUserData(state.data.userData)
    }

    data class WaterAppState(
        val userData: WaterAppUserData = WaterAppUserData()
    ): State

    sealed class WaterAppEvents : Event

    data class WaterAppUserData(
        val gender: String = "",
        val height: String = "",
        val weight: String = "",
        val sleepTime: String = "",
        val wakeUpTime: String = "",
        val sport: String = ""
    )
}