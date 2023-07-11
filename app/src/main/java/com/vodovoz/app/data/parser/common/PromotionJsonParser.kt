package com.vodovoz.app.data.parser.common

import com.vodovoz.app.data.model.common.PromotionEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import com.vodovoz.app.feature.home.viewholders.homebanners.model.BannerAdvEntity
import com.vodovoz.app.feature.home.viewholders.homepromotions.model.PromotionAdvEntity
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
        },
        promotionAdvEntity = parseAdvEntity()
    )

    private fun JSONObject.parseAdvEntity() = when(has("OREKLAME")) {
        true -> {
            when(isNull("OREKLAME")) {
                true -> null
                else -> {
                    PromotionAdvEntity(
                        titleHeader = getJSONObject("OREKLAME").safeString("NAME"),
                        titleAdv = getJSONObject("OREKLAME").safeString("ZAGOLOVOK"),
                        bodyAdv = getJSONObject("OREKLAME").safeString("NAMEVNUTRI"),
                        dataAdv = getJSONObject("OREKLAME").safeString("DANNYE"),
                    )
                }
            }
        }
        false -> null
    }

}