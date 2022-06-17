package com.vodovoz.app.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.data.parser.response.CategoryDetailResponseJsonParser
import retrofit2.HttpException

class ProductListPagingSource(
    private val categoryId: Long,
    private val sort: String,
    private val orientation: String,
    private val filter: String,
    private val filterValue: String,
    private val priceFrom: Int,
    private val priceTo: Int,
    private val remoteData: RemoteDataSource,
    private val categoryDetailResponseJsonParser: CategoryDetailResponseJsonParser
) : PagingSource<Int, ProductEntity>() {


    override fun getRefreshKey(state: PagingState<Int, ProductEntity>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
        return anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductEntity> {
        try {
            val pageIndex = params.key ?: 1
            val response = remoteData.fetchCategoryDetailResponse(
                categoryId = categoryId,
                pageIndex = pageIndex,
                sort = sort,
                orientation = orientation,
                filter = filter,
                filterValue = filterValue,
                priceFrom = priceFrom,
                priceTo = priceTo
            )

            return if (response.isSuccessful) {
                val categoryDetail = categoryDetailResponseJsonParser.parseResponse(response.body()!!).data
                val nextPageNumber = if (categoryDetail!!.productEntityList.isEmpty()) null else pageIndex + 1
                val prevPageNumber = if (pageIndex > 1) pageIndex - 1 else null
                LoadResult.Page(categoryDetail.productEntityList, prevPageNumber, nextPageNumber)
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

}