package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.core.network.messageWithCode
import com.vodovoz.app.domain.general.model.RequestException
import com.vodovoz.app.util.extensions.catchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take
import retrofit2.Response

 fun <T, R> executeRequest(
    request: suspend () -> Response<T>,
    mapper: (T) -> R,
    onFail: ((Response<T>) -> Result<R>)? = null,
): Flow<Result<R>> {
    return flow {
        val response = request()

        if (response.isSuccessful && response.body() != null) {
            val result = mapper(response.body()!!)
            emit(Result.success(result))
        } else if (onFail != null) {
            emit(onFail(response))
        } else {
            throw RequestException(response.messageWithCode())
        }
    }.catchResult().take(1).flowOn(Dispatchers.IO)
}