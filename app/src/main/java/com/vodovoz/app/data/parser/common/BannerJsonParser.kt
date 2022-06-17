package com.vodovoz.app.data.parser.common

import com.vodovoz.app.data.model.common.BannerEntity
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import org.json.JSONArray
import org.json.JSONObject

object BannerJsonParser {

    fun JSONArray.parseBannerEntityList(): List<BannerEntity> = mutableListOf<BannerEntity>().apply {
        for (index in 0 until length()) {
            add(getJSONObject(index).parseBannerEntity())
        }
    }

    fun JSONObject.parseBannerEntity() = BannerEntity(
        id = getLong("ID"),
        name = getString("NAME"),
        detailPicture = getString("DETAIL_PICTURE").parseImagePath()
    )

}