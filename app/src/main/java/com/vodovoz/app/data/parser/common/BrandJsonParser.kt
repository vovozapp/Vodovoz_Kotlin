package com.vodovoz.app.data.parser.common

import com.vodovoz.app.data.model.common.BrandEntity
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import org.json.JSONArray
import org.json.JSONObject

object BrandJsonParser {

    fun JSONArray.parseBrandEntityList(): List<BrandEntity> = mutableListOf<BrandEntity>().apply {
        for (index in 0 until length()) {
            add(getJSONObject(index).parserBrandEntity())
        }
    }

    fun JSONObject.parserBrandEntity() = BrandEntity(
        id = safeLong("ID"),
        name = safeString("NAME"),
        detailPicture = safeString("DETAIL_PICTURE").parseImagePath(),
        hasList = safeString("SSILKA") != "" && safeString("SSILKA") == "Y"
    )

}