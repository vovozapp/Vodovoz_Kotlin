package com.vodovoz.app.ui.base

sealed class ViewState {
    class Loading : ViewState()
    class Error(val errorMessage: String) : ViewState()
    class Success : ViewState()
    class Hide : ViewState()
}