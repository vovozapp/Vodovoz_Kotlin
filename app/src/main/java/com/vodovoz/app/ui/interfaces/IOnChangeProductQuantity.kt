package com.vodovoz.app.ui.interfaces

fun interface IOnChangeProductQuantity {
    fun onChangeProductQuantity(pair: Pair<Long, Int>)
}