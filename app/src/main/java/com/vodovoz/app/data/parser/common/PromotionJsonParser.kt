package com.vodovoz.app.data.parser.common

import com.vodovoz.app.data.model.common.PromotionEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import org.json.JSONArray
import org.json.JSONObject

object PromotionJsonParser {

    fun JSONArray.parsePromotionEntityList(): List<PromotionEntity> = mutableListOf<PromotionEntity>().apply {
        for (index in 0 until length()) {
            add(getJSONObject(index).parsePromotionEntity())
        }
    }

    fun JSONObject.parsePromotionEntity() = PromotionEntity(
        id = getLong("ID"),
        name = getString("NAME"),
        detailPicture = getString("DETAIL_PICTURE").parseImagePath(),
        customerCategory = when(has("NAMERAZDEL")){
            true -> getString("NAMERAZDEL")
            false -> null
        },
        statusColor = when(has("CVET")){
            true -> getString("CVET")
            false -> null
        },
        timeLeft = getString("DATAOUT"),
        productEntityList = when(isNull("TOVAR")) {
            true -> listOf()
            false -> getJSONArray("TOVAR").parseProductEntityList()
        }
    )

}