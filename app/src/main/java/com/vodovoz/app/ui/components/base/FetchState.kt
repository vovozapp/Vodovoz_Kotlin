package com.vodovoz.app.ui.components.base

sealed class FetchState<T>() {
    class Loading<T> : FetchState<T>()
    class Error<T>(val errorMessage: String?) : FetchState<T>()
    class Success<T>(val data: T? = null) : FetchState<T>()
    class Hide<T> : FetchState<T>()
}