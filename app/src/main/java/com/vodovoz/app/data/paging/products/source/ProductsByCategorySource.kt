package com.vodovoz.app.data.paging.products.source

import com.vodovoz.app.data.LocalSyncExtensions.syncCartQuantity
import com.vodovoz.app.data.LocalSyncExtensions.syncFavoriteProducts
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.paginatedProducts.ProductsByCategoryResponseJsonParser.parseProductsByCategoryResponse
import com.vodovoz.app.data.remote.RemoteDataSource
import okhttp3.ResponseBody
import retrofit2.Response

class ProductsByCategorySource(
    private val categoryId: Long,
    private val sort: String,
    private val orientation: String,
    private val filter: String,
    private val filterValue: String,
    private val priceFrom: Int,
    private val priceTo: Int,
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : ProductsSource {

    override suspend fun getResponse(page: Int) = remoteDataSource.fetchProductsByCategory(
        categoryId = categoryId,
        sort = sort,
        orientation = orientation,
        filter = filter,
        filterValue = filterValue,
        priceFrom = priceFrom,
        priceTo = priceTo,
        page = page
    )

    override fun parseResponse(response: Response<ResponseBody>) = when(val body = response.body()) {
        null -> ResponseEntity.Error("Empty body")
        else -> body.parseProductsByCategoryResponse().apply {
            if (this is ResponseEntity.Success) {
                this.data.syncFavoriteProducts(localDataSource)
                this.data.syncCartQuantity(localDataSource)
            }
        }
    }

}