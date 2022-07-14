package com.vodovoz.app.ui.interfaces

fun interface IOnFavoriteClick {
    fun onFavoriteClick(pair: Pair<Long, Boolean>)
}