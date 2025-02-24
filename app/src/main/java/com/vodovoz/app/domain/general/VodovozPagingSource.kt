package com.vodovoz.app.domain.general

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.vodovoz.app.data.vodovoz_service.mappers.executeRequest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.singleOrNull
import retrofit2.Response

class VodovozPagingSource<T : Any, R : Any>(
    private val request: suspend (page: Int, limit: Int) -> Response<T>,
    private val mapper: (T) -> List<R>,
) : PagingSource<Int, R>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, R> {
        val page = params.key ?: 1

        val result = executeRequest(
            request = {
                request(page, params.loadSize)
            },
            mapper = { body ->
                mapper(body)
            }
        ).singleOrNull() ?: return LoadResult.Error(NoSuchElementException("No elements received from the flow"))

        result.onSuccess { list ->
            val nextKey = if (list.size < 2) null else page + 1

            return LoadResult.Page(
                data = list,
                prevKey = if (page == 1) null else page - 1,
                nextKey = nextKey
            )
        }.onFailure { throwable ->
            return LoadResult.Error(throwable)
        }

        return LoadResult.Invalid()
    }

    override fun getRefreshKey(state: PagingState<Int, R>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}