package com.vodovoz.app.data.parser.response.viewed

import android.util.Log
import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.util.LogSettings
import okhttp3.ResponseBody
import org.json.JSONObject

object ViewedProductSliderResponseJsonParser {

    fun ResponseBody.parseViewedProductsSliderResponse(): ResponseEntity<List<CategoryDetailEntity>> {
        val responseJson = JSONObject(this.string())
        Log.i(LogSettings.REQ_RES_LOG, responseJson.toString())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                listOf(CategoryDetailEntity(
                    id = 0,
                    name = responseJson.getJSONObject("data").getString("NAME"),
                    productEntityList = responseJson.getJSONObject("data")
                        .getJSONArray("VIEWTOVAR").parseProductEntityList()
                ))
            )
            else -> ResponseEntity.Hide()
        }
    }

}