package com.vodovoz.app.util.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

fun <T> Flow<Result<T>>.catchResult(): Flow<Result<T>> = catch { throwable ->
    emit(Result.failure(throwable))
}

fun<T> resultFailure(throwable: Throwable = Throwable()) = Result.failure<T>(throwable)