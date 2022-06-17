package com.vodovoz.app.data.parser.response

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

class DiscountCategoryResponseParser {

    fun parseResponse(
        responseBody: ResponseBody
    ): ResponseEntity<List<CategoryDetailEntity>> {
        val responseJson = JSONObject(responseBody.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                mutableListOf<CategoryDetailEntity>().apply {
                    responseJson.getJSONArray("data").parseProductEntityList().let { productList ->
                        add(CategoryDetailEntity(
                            name = responseJson.getString("glavtitle"),
                            productEntityList = productList,
                            productAmount = productList.size
                        ))
                    }
                }
            )
            else -> ResponseEntity.Error()
        }
    }

}