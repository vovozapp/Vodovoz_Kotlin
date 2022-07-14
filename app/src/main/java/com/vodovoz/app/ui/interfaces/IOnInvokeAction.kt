package com.vodovoz.app.ui.interfaces

import com.vodovoz.app.data.model.common.ActionEntity

fun interface IOnInvokeAction {
    fun onInvokeAction(actionEntity: ActionEntity)
}