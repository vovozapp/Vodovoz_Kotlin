package com.vodovoz.app.data.parser.response

import com.vodovoz.app.data.model.common.BannerEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.BannerJsonParser.parseBannerEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

class BannerResponseJsonParser() {

    fun parseResponse(
        responseBody: ResponseBody
    ): ResponseEntity<List<BannerEntity>> {
        val responseJson = JSONObject(responseBody.string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getJSONArray("data").parseBannerEntityList())
            else -> ResponseEntity.Error()
        }
    }

}