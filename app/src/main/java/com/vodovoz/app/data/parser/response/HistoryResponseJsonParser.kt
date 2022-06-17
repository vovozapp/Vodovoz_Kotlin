package com.vodovoz.app.data.parser.response

import com.vodovoz.app.data.model.common.HistoryEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.HistoryJsonParser
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

class HistoryResponseJsonParser(
    private val historyJsonParser: HistoryJsonParser
) {

    fun parseResponse(
        responseBody: ResponseBody
    ): ResponseEntity<List<HistoryEntity>> {
        val responseJson = JSONObject(responseBody.string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                historyJsonParser.parseHistoryEntityList(responseJson.getJSONArray("data")))
            else -> ResponseEntity.Error()
        }
    }

}