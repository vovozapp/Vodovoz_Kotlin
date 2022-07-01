package com.vodovoz.app.data.parser.response.viewed

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object ViewedProductSliderResponseJsonParser {

    fun ResponseBody.parseViewedProductsSliderResponse(): ResponseEntity<List<CategoryDetailEntity>> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                listOf(CategoryDetailEntity(
                    name = responseJson.getJSONObject("data").getString("NAME"),
                    productEntityList = responseJson.getJSONObject("data")
                        .getJSONArray("VIEWTOVAR").parseProductEntityList()
                ))
            )
            else -> ResponseEntity.Error(responseJson.getString("message"))
        }
    }

}