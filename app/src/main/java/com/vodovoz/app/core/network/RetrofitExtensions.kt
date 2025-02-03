package com.vodovoz.app.core.network

import retrofit2.Response


fun<T> Response<T>.messageWithCode(): String {
    return "${message()} - ${code()}"
}