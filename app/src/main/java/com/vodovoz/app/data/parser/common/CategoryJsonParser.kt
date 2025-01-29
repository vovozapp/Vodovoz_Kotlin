package com.vodovoz.app.data.parser.common

import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.data.model.common.CatalogBanner
import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import org.json.JSONArray
import org.json.JSONObject

object CategoryJsonParser {


    fun JSONArray.parseCategoryEntityList(): List<CategoryEntity> =
        mutableListOf<CategoryEntity>().apply {
            for (index in 0 until length()) {
                add(getJSONObject(index).parseCategoryEntity())
            }
        }

    private fun JSONObject.parseCategoryEntity() = CategoryEntity(
        id = getLong("ID"),
        name = getString("NAME"),
        detailPicture = this.parseDetailImage(),
        subCategoryEntityList = this.parseSubCategoryEntityList(),
        actionEntity = when (safeString("UF_SILKAPEREXOD")) {
            "pokypkasertificat" -> ActionEntity.BuyCertificate()
            else -> null
        }
    )

    fun JSONObject.parseDetailImage() = when (has("PICTURE")) {
        true -> when (isNull("PICTURE")) {
            true -> null
            else -> getString("PICTURE").parseImagePath()
        }

        false -> null
    }

    private fun JSONObject.parseSubCategoryEntityList() = when (has("PODRAZDEL")) {
        true -> getJSONArray("PODRAZDEL").parseCategoryEntityList()
        else -> listOf()
    }

    fun JSONObject.parseCategoryBanner(): CatalogBanner = CatalogBanner(
        text = getString("NAME"),
        textColor = getString("COLORTEXT"),
        backgroundColor = getString("BACGROUND"),
        iconUrl = this.parseBannerIcon(),
        actionEntity = this.parseActionBanner()
    )

    private fun JSONObject.parseBannerIcon() = when (has("IKONKA")) {
        true -> when (isNull("IKONKA")) {
            true -> null
            else -> getString("IKONKA")
        }

        false -> null
    }

    private fun JSONObject.parseActionBanner(): ActionEntity? {
        if (has("URL") && !isNull("URL") && getString("URL").isNotEmpty()) {
            return ActionEntity.Link(
                url = getString("URL")
            )
        } else if (has("PEREXODID") && !isNull("PEREXODID")) {
            return when (getString("PEREXODID")) {
                "vseskidki" -> ActionEntity.Discount()
                "vsenovinki" -> ActionEntity.Novelties()
                "vseakcii" -> ActionEntity.AllPromotions()
                "trekervodi" -> ActionEntity.WaterApp()
                "dostavka" -> ActionEntity.Delivery()
                "profil" -> ActionEntity.Profile()
                "pokypkasertificat" -> ActionEntity.BuyCertificate()
                else -> null
            }
        }
        return null
    }


}