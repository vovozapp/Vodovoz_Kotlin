package com.vodovoz.app.util.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

fun <T> Flow<Result<T>>.catchResult(throwError: Boolean = false): Flow<Result<T>> = catch { throwable ->
    if (throwError) throw throwable else emit(Result.failure(throwable))
}