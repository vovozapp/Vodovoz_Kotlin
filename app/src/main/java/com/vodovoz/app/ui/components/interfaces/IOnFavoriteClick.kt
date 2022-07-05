package com.vodovoz.app.ui.components.interfaces

fun interface IOnFavoriteClick {
    fun onFavoriteClick(pair: Pair<Long, Boolean>)
}