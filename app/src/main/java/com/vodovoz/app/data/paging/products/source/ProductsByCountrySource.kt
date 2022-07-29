package com.vodovoz.app.data.paging.products.source

import com.vodovoz.app.data.LocalSyncExtensions.syncCartQuantity
import com.vodovoz.app.data.LocalSyncExtensions.syncFavoriteProducts
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.paginatedProducts.ProductsByCountryResponseJsonParser.parseProductsByCountryResponse
import com.vodovoz.app.data.remote.RemoteDataSource
import okhttp3.ResponseBody

class ProductsByCountrySource(
    private val countryId: Long,
    private val sort: String? = null,
    private val orientation: String? = null,
    private val categoryId: Long? = null,
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : ProductsSource {

    override suspend fun getResponse(page: Int) = remoteDataSource.fetchProductsByCountry(
        countryId = countryId,
        sort = sort,
        orientation = orientation,
        categoryId = categoryId,
        page = page
    )

    override fun parseResponse(response: retrofit2.Response<ResponseBody>) = when(val body = response.body()) {
        null -> ResponseEntity.Error("Empty body")
        else -> body.parseProductsByCountryResponse().apply {
            if (this is ResponseEntity.Success) {
                this.data.syncFavoriteProducts(localDataSource)
                this.data.syncCartQuantity(localDataSource)
            }
        }
    }

}