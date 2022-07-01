package com.vodovoz.app.data.paging.source

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
    private val remoteData: RemoteDataSource
) : ProductsSource {

    override suspend fun getResponse(page: Int) = remoteData.fetchProductsByCategory(
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
        else -> body.parseProductsByCategoryResponse()
    }

}