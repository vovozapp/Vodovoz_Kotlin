package com.vodovoz.app.data.parser.response

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

class NoveltiesCategoryResponseParser {

    fun parseResponse(
        responseBody: ResponseBody
    ): ResponseEntity<List<CategoryDetailEntity>> {
        val responseJson = JSONObject(responseBody.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                mutableListOf<CategoryDetailEntity>().apply {
                    val productEntityList = responseJson.getJSONArray("data").parseProductEntityList()
                    add(CategoryDetailEntity(
                        id = 0,
                        name = responseJson.getString("glavtitle"),
                        productEntityList = productEntityList,
                        productAmount = productEntityList.size
                    ))
                }
            )
            else -> ResponseEntity.Error()
        }
    }

}