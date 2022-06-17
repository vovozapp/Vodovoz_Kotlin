package com.vodovoz.app.data.parser.common

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import org.json.JSONObject

class CategoryDetailJsonParser {

    fun parseCategoryDetailEntity(
        categoryJson: JSONObject
    ) = CategoryDetailEntity(
        id = 0,
        name = categoryJson.getString("titlerazdel"),
        productAmount = categoryJson.getInt("count"),
        productEntityList = categoryJson.getJSONArray("data").parseProductEntityList()
    )

}