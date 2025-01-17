package com.vodovoz.app.feature.cart.ordering.intervals.adapter

import com.vodovoz.app.ui.model.PayMethodUI
import com.vodovoz.app.ui.model.ShippingAlertUI
import com.vodovoz.app.ui.model.ShippingIntervalUI

interface IntervalsClickListener {

    fun onAlertClick(item: ShippingAlertUI) = Unit
    fun onIntervalClick(item: ShippingIntervalUI) = Unit
    fun onPayMethodClick(item: PayMethodUI) = Unit
    fun onGenderClick(gender: String) = Unit
}