package com.vodovoz.app.data.parser.response.paginatedProducts

import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object FavoriteProductsResponseJsonParser {

    fun ResponseBody.parseFavoriteProductsResponse(): ResponseEntity<List<ProductEntity>> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                when(responseJson.isNull("data")) {
                    true -> listOf()
                    false -> responseJson.getJSONArray("data").parseProductEntityList()
                }
            )
            else -> ResponseEntity.Success(listOf())
        }
    }

}