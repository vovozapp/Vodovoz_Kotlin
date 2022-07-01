package com.vodovoz.app.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.paging.source.ProductsSource
import retrofit2.HttpException

class ProductsPagingSource(
    private val productsSource: ProductsSource
) : PagingSource<Int, ProductEntity>() {


    override fun getRefreshKey(state: PagingState<Int, ProductEntity>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
        return anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductEntity> {
        try {
            val page = params.key ?: 1
            val response = productsSource.getResponse(page)

            if (!response.isSuccessful) return LoadResult.Error(HttpException(response))

            return when(val parsedResponse = productsSource.parseResponse(response)) {
                is ResponseEntity.Success -> {
                    val productEntityList = parsedResponse.data
                    val nextPageNumber = if (productEntityList.isEmpty()) null else page + 1
                    val prevPageNumber = if (page > 1) page - 1 else null
                    LoadResult.Page(productEntityList, prevPageNumber, nextPageNumber)
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