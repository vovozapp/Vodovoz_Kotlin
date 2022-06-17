package com.vodovoz.app.data.parser.common

import com.vodovoz.app.data.model.common.PromotionEntity
import com.vodovoz.app.data.model.common.PromotionFilterEntity
import org.json.JSONArray
import org.json.JSONObject

object PromotionParser {

    fun JSONArray.parsePromotionFilterEntityList(): List<PromotionFilterEntity> = mutableListOf<PromotionFilterEntity>().apply {
        for (index in 0 until length()) {
            add(getJSONObject(index).parsePromotionFilterEntity())
        }
    }

    fun JSONObject.parsePromotionFilterEntity() = PromotionFilterEntity(
        id = getLong("ID"),
        name = getString("NAME"),
        code = getString("CODE")
    )

}