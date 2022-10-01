package com.vodovoz.app.data.parser.common

import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import org.json.JSONArray
import org.json.JSONObject

object CategoryJsonParser {


    fun JSONArray.parseCategoryEntityList(): List<CategoryEntity> = mutableListOf<CategoryEntity>().apply {
        for (index in 0 until length()) {
            add(getJSONObject(index).parseCategoryEntity())
        }
    }

    fun JSONObject.parseCategoryEntity() = CategoryEntity(
        id = getLong("ID"),
        name = getString("NAME"),
        detailPicture = this.parseDetailImage(),
        subCategoryEntityList = this.parseSubCategoryEntityList()
    )

    private fun JSONObject.parseDetailImage() = when(has("PICTURE")) {
        true -> when(isNull("PICTURE")) {
            true -> null
            else -> getString("PICTURE").parseImagePath()
        }
        false -> null
    }

    private fun JSONObject.parseSubCategoryEntityList() = when (has("PODRAZDEL")) {
        true -> getJSONArray("PODRAZDEL").parseCategoryEntityList()
        else -> listOf()
    }

}