package com.vodovoz.app.data.model.common

sealed class ResponseEntity<T>(
    val data: T? = null
) {
    class Success<T>(data: T) : ResponseEntity<T>(data)
    class Error<T>(val errorMessage: String? = null) : ResponseEntity<T>(null)
}