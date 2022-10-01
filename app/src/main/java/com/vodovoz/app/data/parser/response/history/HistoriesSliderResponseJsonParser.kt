package com.vodovoz.app.data.parser.response.history

import com.vodovoz.app.data.model.common.HistoryEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.HistoryJsonParser.parseHistoryEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object HistoriesSliderResponseJsonParser {

    fun ResponseBody.parseHistoriesSliderResponse(): ResponseEntity<List<HistoryEntity>> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                responseJson.getJSONArray("data").parseHistoryEntityList()
            )
            else -> ResponseEntity.Error("Ошибка парсинга истории")
        }
    }

}