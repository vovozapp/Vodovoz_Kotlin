package com.vodovoz.app.data.parser.response

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.CategoryDetailJsonParser
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

class CategoryDetailResponseJsonParser(
    private val categoryDetailJsonParser: CategoryDetailJsonParser
) {

    fun parseResponse(
        responseBody: ResponseBody
    ): ResponseEntity<CategoryDetailEntity> {
        val responseJson = JSONObject(responseBody.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(categoryDetailJsonParser.parseCategoryDetailEntity(responseJson))
            else -> ResponseEntity.Error()
        }
    }

}