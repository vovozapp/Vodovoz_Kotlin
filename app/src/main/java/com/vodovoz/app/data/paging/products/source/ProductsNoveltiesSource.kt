package com.vodovoz.app.data.paging.products.source

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.paginatedProducts.ProductsNoveltiesResponseJsonParser.parseProductsNoveltiesResponse
import com.vodovoz.app.data.remote.RemoteDataSource
import okhttp3.ResponseBody
import retrofit2.Response

class ProductsNoveltiesSource(
    private val categoryId: Long?,
    private val sort: String?,
    private val orientation: String?,
    private val remoteDataSource: RemoteDataSource
) : ProductsSource {

    override suspend fun getResponse(page: Int) = remoteDataSource.fetchProductsNovelties(
        categoryId = categoryId,
        sort = sort,
        orientation = orientation,
        page = page
    )

    override fun parseResponse(response: Response<ResponseBody>) = when(val body = response.body()) {
        null -> ResponseEntity.Error("Empty body")
        else -> body.parseProductsNoveltiesResponse()
    }

}