package com.vodovoz.app.feature.cart.gifts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.feature.cart.gifts.model.GiftsEvent
import com.vodovoz.app.feature.cart.gifts.model.GiftsState
import com.vodovoz.app.feature.cart.model.CartPresentItemUi
import com.vodovoz.app.feature.cart.model.CartPresentPopupWindowUi
import com.vodovoz.app.ui.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GiftsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : MviViewModel<GiftsState, GiftsEvent>(GiftsState()) {

    private val giftDetails: CartPresentPopupWindowUi? =
        savedStateHandle.get<CartPresentPopupWindowUi>("popupWindow")

    init {
        initializeData()
    }

    private fun initializeData() = viewModelScope.launch {
        if (giftDetails == null) {
            navigateBack()
            return@launch
        }

        _state.update { s ->
            s.copy(
                button = giftDetails.button,
                gifts = giftDetails.items,
                currentGift = giftDetails.items.firstOrNull() ?: s.currentGift
            )
        }
    }

    fun navigateBack() = viewModelScope.launch {
        _events.emit(GiftsEvent.GoBack)
    }

    fun chooseGift() = viewModelScope.launch {
        _events.emit(GiftsEvent.GoToCart(stateSnapshot.currentGift))
    }

    fun selectGift(presentItem: CartPresentItemUi) = viewModelScope.launch {
        _state.update { s ->
            s.copy(currentGift = presentItem)
        }
    }


}