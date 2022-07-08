package com.vodovoz.app.data.paging.orders

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.vodovoz.app.data.model.common.OrderEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.order.AllOrdersResponseJsonParser.parseAllOrdersSliderResponse
import com.vodovoz.app.data.remote.RemoteDataSource
import com.vodovoz.app.util.LogSettings
import retrofit2.HttpException

class OrdersPagingSource(
    private val userId: Long?,
    private val appVersion: String?,
    private val orderId: Long?,
    private val status: String?,
    private val remoteDataSource: RemoteDataSource
) : PagingSource<Int, OrderEntity>() {

    override fun getRefreshKey(state: PagingState<Int, OrderEntity>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
        return anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OrderEntity> {
        try {
            val page = params.key ?: 1
            Log.i(LogSettings.ID_LOG, "STATUSES ${status}")
            Log.i(LogSettings.ID_LOG, "ID ${orderId}")
            val response = remoteDataSource.fetchAllOrders(
                userId = userId,
                appVersion = appVersion,
                orderId = orderId,
                status = status,
                page = page
            )

            if (!response.isSuccessful) return LoadResult.Error(HttpException(response))

            return when(val parsedResponse = response.body()!!.parseAllOrdersSliderResponse()) {
                is ResponseEntity.Success -> {
                    val orderEntityList = parsedResponse.data
                    val nextPageNumber = if (orderEntityList.isEmpty()) null else page + 1
                    val prevPageNumber = if (page > 1) page - 1 else null
                    LoadResult.Page(orderEntityList, prevPageNumber, nextPageNumber)
                }
                is ResponseEntity.Hide -> LoadResult.Error(Exception("Hide content"))
                is ResponseEntity.Error -> LoadResult.Error(Exception(parsedResponse.errorMessage))
            }
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

}