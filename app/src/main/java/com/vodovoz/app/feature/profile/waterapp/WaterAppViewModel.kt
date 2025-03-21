package com.vodovoz.app.feature.profile.waterapp

import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.feature.profile.waterapp.model.WaterAppUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaterAppViewModel @Inject constructor(
    private val waterAppHelper: WaterAppHelper,
    private val moshi: Moshi,
) : PagingContractViewModel<WaterAppViewModel.WaterAppState, WaterAppViewModel.WaterAppEvents>(
    WaterAppState()
) {

    fun navigateBack() = viewModelScope.launch {
        eventListener.emit(WaterAppEvents.GoBack)
    }

    fun moveToUserFields() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                uiState = WaterAppUiState.UserData.Gender
            )
        }
    }

    fun selectGender(man: Boolean) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                userData = s.userData.copy(
                    gender = if (man) "man" else "girl"
                )
            )
        }
    }

    data class WaterAppState(
        val userData: WaterAppHelper.WaterAppUserData = WaterAppHelper.WaterAppUserData(),
        val uiState: WaterAppUiState = WaterAppUiState.UserData.Height,
    ) : State

    sealed class WaterAppEvents : Event {

        data object GoBack : WaterAppEvents()

    }


}