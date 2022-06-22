package com.vodovoz.app.data.paging.source

import android.util.Log
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.paginatedProducts.ProductsByBrandResponseJsonParser.parseProductsByBrandResponse
import com.vodovoz.app.data.parser.response.paginatedProducts.ProductsBySliderResponseJsonParser.parseProductsBySliderResponse
import com.vodovoz.app.data.parser.response.paginatedProducts.ProductsDiscountResponseJsonParser.parseProductsDiscountResponse
import com.vodovoz.app.data.remote.RemoteDataSource
import com.vodovoz.app.util.LogSettings
import okhttp3.ResponseBody
import retrofit2.Response

class ProductsBySliderSource(
    private val categoryId: Long?,
    private val sort: String?,
    private val orientation: String?,
    private val remoteData: RemoteDataSource
) : ProductsSource {

    init {
        Log.i(LogSettings.ID_LOG, "CREATE SLIDER SOURCE ID $categoryId")
    }

    override suspend fun getResponse(page: Int) = remoteData.fetchProductsBySlider(
        categoryId = categoryId,
        sort = sort,
        orientation = orientation,
        page = page
    )

    override fun parseResponse(response: Response<ResponseBody>) = when(val body = response.body()) {
        null -> ResponseEntity.Error("Empty body")
        else -> body.parseProductsBySliderResponse()
    }

}