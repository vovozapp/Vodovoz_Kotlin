package com.vodovoz.app.ui.components.interfaces

import com.vodovoz.app.data.model.common.ActionEntity

fun interface IOnBannerClick {
    fun onBannerClick(actionEntity: ActionEntity)
}