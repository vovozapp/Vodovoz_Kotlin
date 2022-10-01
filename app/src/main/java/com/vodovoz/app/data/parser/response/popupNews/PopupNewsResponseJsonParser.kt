package com.vodovoz.app.data.parser.response.popupNews

import android.util.Log
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.data.model.common.PopupNewsEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import com.vodovoz.app.util.LogSettings
import okhttp3.ResponseBody
import org.json.JSONObject

object PopupNewsResponseJsonParser {

    fun ResponseBody.parsePopupNewsResponse(): ResponseEntity<PopupNewsEntity> {
        val responseJson = JSONObject(this.string())
        Log.d(LogSettings.RESPONSE_BODY_LOG, responseJson.toString(2))
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                responseJson.getJSONObject("data").parsePopupNewsEntity()
            )
            else -> ResponseEntity.Error("Ошибка парсинга вспылвающая новость")
        }
    }

    private fun JSONObject.parsePopupNewsEntity() = PopupNewsEntity(
        name = getString("tile"),
        detailText = getString("text"),
        detailPicture = getString("Picture").parseImagePath(),
        actionEntity = getJSONObject("HARAKTERISTIK").parseBannerActionEntity()
    )

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