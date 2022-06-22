package com.vodovoz.app.data.parser.common

import com.vodovoz.app.data.model.common.HistoryEntity
import com.vodovoz.app.data.parser.common.BannerJsonParser.parseBannerEntityList
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import org.json.JSONArray
import org.json.JSONObject

object HistoryJsonParser {

    fun JSONArray.parseHistoryEntityList(): List<HistoryEntity> = mutableListOf<HistoryEntity>().apply {
        for (index in 0 until length()) {
            add(getJSONObject(index).parserHistoryEntity())
        }
    }

    fun JSONObject.parserHistoryEntity() = HistoryEntity(
        id = getString("ID").toLong(),
        detailPicture = getJSONObject("RAZDEL").getString("IMAGE").parseImagePath(),
        bannerEntityList = getJSONArray("VNYTRENNOST").parseBannerEntityList()
    )

}