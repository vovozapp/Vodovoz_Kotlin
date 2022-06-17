package com.vodovoz.app.data.parser.response

import com.vodovoz.app.data.model.common.BrandEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.BrandJsonParser.parseBrandEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

class BrandResponseJsonParser {

    fun parseResponse(
        responseBody: ResponseBody
    ): ResponseEntity<List<BrandEntity>> {
        val responseJson = JSONObject(responseBody.string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getJSONArray("data").parseBrandEntityList())
            else -> ResponseEntity.Error()
        }
    }

}