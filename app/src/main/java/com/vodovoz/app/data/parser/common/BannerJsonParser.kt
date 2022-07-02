package com.vodovoz.app.data.parser.common

import com.vodovoz.app.data.model.common.ActionEntity
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
        detailPicture = parseDetailImage(),
        actionEntity = getJSONObject("HARAKTERISTIK").parseBannerActionEntity()
    )

    private fun JSONObject.parseDetailImage() =
        if (has("DETAIL_PICTURE")) getString("DETAIL_PICTURE").parseImagePath()
        else if (has("IMAGE")) getString("IMAGE").parseImagePath()
        else ""

    private fun JSONObject.parseBannerActionEntity() = when(getString("id")) {
        "TOVAR" -> parseProductBannerActionEntity()
        "TOVARY" -> parseProductsBannerActionEntity()
        "ACTION" -> parsePromotionBannerActionEntity()
        "ACTIONS" -> parsePromotionsBannerActionEntity()
        "RAZDEL" -> parseCategoryBannerActionEntity()
        "BRAND" -> parseBrandActionEntity()
        "BRANDY" -> parseBrandsActionEntity()
        "SSILKA" -> parseLinkBannerActionEntity()
        "DANNYEVSE" -> parseCustomBannerActionEntity()
        else -> null
    }

    private fun JSONObject.parseAction() = when(has("KNOPKA")) {
        true -> getJSONObject("KNOPKA").getString("NAME")
        false -> null
    }

    private fun JSONObject.parseActionColor() = when(has("KNOPKA")) {
        true -> getJSONObject("KNOPKA").getString("COLOR")
        false -> null
    }

    private fun JSONObject.parseBrandsActionEntity() = ActionEntity.Brands(
        brandIdList = mutableListOf<Long>().apply {
            val idJsonArray = getJSONObject("DANNYE").getJSONArray("BRANDY")
            for (index in 0 until idJsonArray.length()) {
                add(idJsonArray.getString(index).toLong())
            }
        }.toList(),
        action = parseAction(),
        actionColor = parseActionColor()
    )

    private fun JSONObject.parseCustomBannerActionEntity() = when(getJSONObject("DANNYE").getString("DANNYEVSE")) {
        "vseskidki" -> ActionEntity.Discount(
            action = parseAction(),
            actionColor = parseActionColor()
        )
        "vsenovinki" -> ActionEntity.Novelties(
            action = parseAction(),
            actionColor = parseActionColor()
        )
        "vseakcii" -> ActionEntity.AllPromotions(
            action = parseAction(),
            actionColor = parseActionColor()
        )
        else -> null
    }

    private fun JSONObject.parseProductsBannerActionEntity() = ActionEntity.Products(
        categoryId = getJSONObject("DANNYE").getLong("TOVARY"),
        action = parseAction(),
        actionColor = parseActionColor()
    )

    private fun JSONObject.parseProductBannerActionEntity() = ActionEntity.Product(
        productId = getJSONObject("DANNYE").getLong("TOVAR"),
        action = parseAction(),
        actionColor = parseActionColor()
    )

    private fun JSONObject.parsePromotionsBannerActionEntity() = ActionEntity.Promotions(
        categoryId = getJSONObject("DANNYE").getLong("ACTIONS"),
        action = parseAction(),
        actionColor = parseActionColor()
    )

    private fun JSONObject.parsePromotionBannerActionEntity() = ActionEntity.Promotion(
        promotionId = getJSONObject("DANNYE").getLong("ACTION"),
        action = parseAction(),
        actionColor = parseActionColor()
    )

    private fun JSONObject.parseBrandActionEntity() = ActionEntity.Brand(
        brandId = getJSONObject("DANNYE").getJSONObject("BRAND").getLong("ID"),
        action = parseAction(),
        actionColor = parseActionColor()
    )

    private fun JSONObject.parseLinkBannerActionEntity() = ActionEntity.Link(
        url = getJSONObject("DANNYE").getString("SSILKA"),
        action = parseAction(),
        actionColor = parseActionColor()
    )

    private fun JSONObject.parseCategoryBannerActionEntity() = ActionEntity.Category(
        categoryId = getJSONObject("DANNYE").getJSONObject("RAZDEL").getLong("ID"),
        action = parseAction(),
        actionColor = parseActionColor()
    )

}