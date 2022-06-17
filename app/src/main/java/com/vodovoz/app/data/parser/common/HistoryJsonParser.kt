package com.vodovoz.app.data.parser.common

import com.vodovoz.app.data.model.common.HistoryEntity
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import org.json.JSONArray
import org.json.JSONObject

class HistoryJsonParser {

    fun parseHistoryEntityList(
        historyJSONArray: JSONArray
    ): List<HistoryEntity> = mutableListOf<HistoryEntity>().apply {
        for (index in 0 until historyJSONArray.length()) {
            add(parserHistoryEntity(historyJSONArray.getJSONObject(index)))
        }
    }

    fun parserHistoryEntity(
        historyJson: JSONObject
    ) = HistoryEntity(
        id = historyJson.getString("ID"),
        detailPicture = historyJson.getJSONObject("RAZDEL").getString("IMAGE").parseImagePath()
    )

}