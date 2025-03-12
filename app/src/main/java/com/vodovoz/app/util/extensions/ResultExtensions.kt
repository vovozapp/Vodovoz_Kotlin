package com.vodovoz.app.util.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.singleOrNull

fun <T> Flow<Result<T>>.catchResult(): Flow<Result<T>> = catch { throwable ->
    emit(Result.failure(throwable))
}

fun<T> resultFailure(throwable: Throwable = Throwable()) = Result.failure<T>(throwable)

suspend fun<T> Flow<Result<T>>.singleResult() = singleOrNull() ?: resultFailure(NoSuchElementException("Result flow is empty"))