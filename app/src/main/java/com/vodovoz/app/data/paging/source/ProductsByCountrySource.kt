package com.vodovoz.app.data.paging.source

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.paginatedProducts.ProductsByCountryResponseJsonParser.parseProductsByCountryResponse
import com.vodovoz.app.data.remote.RemoteDataSource
import okhttp3.ResponseBody

class ProductsByCountrySource(
    private val countryId: Long,
    private val sort: String? = null,
    private val orientation: String? = null,
    private val categoryId: Long? = null,
    private val remoteData: RemoteDataSource
) : ProductsSource {

    override suspend fun getResponse(page: Int) = remoteData.fetchProductsByCountry(
        countryId = countryId,
        sort = sort,
        orientation = orientation,
        categoryId = categoryId,
        page = page
    )

    override fun parseResponse(response: retrofit2.Response<ResponseBody>) = when(val body = response.body()) {
        null -> ResponseEntity.Error("Empty body")
        else -> body.parseProductsByCountryResponse()
    }

}