package com.vodovoz.app.data.paging.source

import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import okhttp3.ResponseBody
import retrofit2.Response

interface ProductsSource {

    abstract suspend fun getResponse(page: Int): Response<ResponseBody>
    abstract fun parseResponse(response: Response<ResponseBody>): ResponseEntity<List<ProductEntity>>

}