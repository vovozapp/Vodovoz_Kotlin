package com.vodovoz.app.feature.cancel_order_fragment.model

sealed class CancelOrderEvent{

    data object GoBack: CancelOrderEvent()

}
