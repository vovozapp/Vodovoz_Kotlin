package com.vodovoz.app.ui.fragment.ordering

import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.ui.base.BaseViewModel

class OrderingViewModel(
    private val dataRepository: DataRepository
) : BaseViewModel() {

    var orderType = OrderType.PERSONAL

}