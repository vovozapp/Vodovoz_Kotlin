package com.vodovoz.app.data.paging.products.source

import com.vodovoz.app.data.LocalSyncExtensions.syncCartQuantity
import com.vodovoz.app.data.LocalSyncExtensions.syncFavoriteProducts
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.paginatedProducts.PastPurchasesProductsResponseJsonParser.parsePastPurchasesProductsResponse
import com.vodovoz.app.data.remote.RemoteDataSource
import okhttp3.ResponseBody
import retrofit2.Response

class PastPurchasesProductsSource(
    private val userId: Long?,
    private val categoryId: Long?,
    private val sort: String?,
    private val orientation: String?,
    private val isAvailable: Boolean?,
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : ProductsSource {

    override suspend fun getResponse(page: Int) = remoteDataSource.fetchPastPurchasesProducts(
        userId = userId,
        categoryId = categoryId,
        sort = sort,
        orientation = orientation,
        page = page,
        isAvailable = isAvailable
    )

    override fun parseResponse(response: Response<ResponseBody>) = when(val body = response.body()) {
        null -> ResponseEntity.Error("Empty body")
        else -> body.parsePastPurchasesProductsResponse().apply {
            if (this is ResponseEntity.Success) {
                this.data.syncFavoriteProducts(localDataSource)
                this.data.syncCartQuantity(localDataSource)
            }
        }
    }

}