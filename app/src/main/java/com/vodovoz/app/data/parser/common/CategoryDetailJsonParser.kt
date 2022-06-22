package com.vodovoz.app.data.parser.common

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import org.json.JSONObject

object CategoryDetailJsonParser {

    fun JSONObject.parseCategoryDetailEntity() = CategoryDetailEntity(
        id = 0,
        name = getString("titlerazdel"),
        productAmount = getInt("count"),
        productEntityList = getJSONArray("data").parseProductEntityList()
    )

}