package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.core.network.messageWithCode
import com.vodovoz.app.data.vodovoz_service.model.VodovozResponseDTO
import com.vodovoz.app.domain.general.model.EmptyResultException
import com.vodovoz.app.domain.general.model.VodovozPlaceholderModel
import com.vodovoz.app.domain.general.model.RequestException
import com.vodovoz.app.util.extensions.catchResult
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import retrofit2.Response

inline fun <T, R> executeRequest(
    crossinline request: suspend () -> Response<T>,
    crossinline mapper: (T) -> R,
    noinline onFail: ((Response<T>) -> Result<R>)? = null,
): Flow<Result<R>> {
    return flow {
        val response = request()
        val body = response.body()

        if (response.isSuccessful && body != null) {
            val result = mapper(body)
            emit(Result.success(result))
        } else if (onFail != null) {
            emit(onFail(response))
        } else {
            val exception = RequestException(response.messageWithCode())
            emit(Result.failure(exception))
        }
    }.catchResult().take(1).onEach { result ->
        result.onFailure { throwable -> debugLog { throwable.stackTraceToString() } }
    }.flowOn(Dispatchers.IO)
}

inline fun <T, R> executeRequest(
    crossinline request: suspend () -> Response<T>,
    crossinline mapper: (T) -> R,
    crossinline onResponse: (Response<T>) -> Unit = {},
    noinline onFail: ((Response<T>) -> Result<R>)? = null,
): Flow<Result<R>> {
    return flow {
        val response = request()
        val body = response.body()

        onResponse(response)
        if (response.isSuccessful && body != null) {
            val result = mapper(body)
            emit(Result.success(result))
        } else if (onFail != null) {
            emit(onFail(response))
        } else {
            val exception = RequestException(response.messageWithCode())
            emit(Result.failure(exception))
        }
    }.catchResult().take(1).onEach { result ->
        result.onFailure { throwable -> debugLog { throwable.stackTraceToString() } }
    }.flowOn(Dispatchers.IO)
}


inline fun <T, R> executeVodovozRequest(
    crossinline request: suspend () -> Response<VodovozResponseDTO<T>>,
    crossinline mapper: (VodovozResponseDTO<T>?) -> R,
    noinline onFail: ((Response<VodovozResponseDTO<T>>) -> Result<R>)? = null,
): Flow<Result<R>> {
    return flow {
        val response = request()
        val body = response.body()

        if (response.isSuccessful) {
            val result = mapper(body)
            emit(Result.success(result))
        } else if (onFail != null) {
            emit(onFail(response))
        } else {
            val exception = RequestException(response.messageWithCode())
            emit(Result.failure(exception))
        }
    }.catchResult().take(1).onEach { result ->
        result.onFailure { throwable -> debugLog { throwable.stackTraceToString() } }
    }.flowOn(Dispatchers.IO)
}


inline fun <T> VodovozResponseDTO<T>.checkError(
    throwError: (VodovozPlaceholderModel) -> Nothing = { it ->
        throw EmptyResultException(errorData = it, message = message ?: "")
    }
) {
    val errorModel = this.error?.toDomain() ?: return
    throwError(errorModel)
}


