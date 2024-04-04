package com.vodovoz.app.data.parser.response.viewed

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object ViewedProductSliderResponseJsonParser {

    fun ResponseBody.parseViewedProductsSliderResponse(): ResponseEntity<CategoryDetailEntity> {
        val responseJson = JSONObject(this.string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                CategoryDetailEntity(
                    id = 0,
                    name = responseJson.getJSONObject("data").safeString("NAME"),
                    productEntityList = responseJson.getJSONObject("data")
                        .getJSONArray("VIEWTOVAR").parseProductEntityList()
                )
            )

            else -> ResponseEntity.Hide()
        }
    }

}