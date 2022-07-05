package com.vodovoz.app.ui.components.interfaces

import com.vodovoz.app.data.model.common.ActionEntity

fun interface IOnInvokeAction {
    fun onInvokeAction(actionEntity: ActionEntity)
}