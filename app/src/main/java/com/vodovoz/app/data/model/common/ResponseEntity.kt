package com.vodovoz.app.data.model.common

sealed class ResponseEntity<T> {
    class Success<T>(val data: T) : ResponseEntity<T>()
    class Error<T>(val errorMessage: String) : ResponseEntity<T>()
    class Hide<T> : ResponseEntity<T>()
}