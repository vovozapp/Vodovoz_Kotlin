package com.vodovoz.app.data.parser.response.paginatedProducts

import android.util.Log
import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.CategoryDetailJsonParser
import com.vodovoz.app.data.parser.common.CategoryDetailJsonParser.parseCategoryDetailEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.util.LogSettings
import okhttp3.ResponseBody
import org.json.JSONObject

object ProductsByCategoryResponseJsonParser {

    fun ResponseBody.parseProductsByCategoryResponse(): ResponseEntity<List<ProductEntity>> {
        val responseJson = JSONObject(string())
        Log.i(LogSettings.REQ_RES_LOG, responseJson.toString())
        return when(responseJson.getString("status")) {
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