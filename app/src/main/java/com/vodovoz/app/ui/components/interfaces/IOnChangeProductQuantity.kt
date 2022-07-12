package com.vodovoz.app.ui.components.interfaces

fun interface IOnChangeProductQuantity {
    fun onChangeProductQuantity(pair: Pair<Long, Int>)
}