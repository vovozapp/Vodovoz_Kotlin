package com.vodovoz.app.data.parser.response.promotion

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.PromotionDetailEntity
import com.vodovoz.app.data.model.common.PromotionEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import com.vodovoz.app.ui.model.PromotionUI
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object PromotionDetailResponseJsonParser {

    fun ResponseBody.parsePromotionDetailResponse(): ResponseEntity<PromotionDetailEntity> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.parsePromotionDetailEntity())
            else -> ResponseEntity.Error(responseJson.getString("message"))
        }
    }

    fun ResponseBody.parsePromotionDetailResponseBundle(): ResponseEntity<PromotionDetailBundle> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(PromotionDetailBundle(detail = responseJson.parsePromotionDetailEntity(), detailError = null))
            else -> ResponseEntity.Success(PromotionDetailBundle(detail = null, detailError = responseJson.parsePromotionDetailErrorEntity()))
        }
    }

    data class PromotionDetailBundle(
        val detail: PromotionDetailEntity?,
        val detailError: PromotionDetailEntityError?
    )

    data class PromotionDetailErrorUI(
        val title: String,
        val message: String,
        val bottomMessage: String,
        val promotionUIList: List<PromotionUI>
    )

    data class PromotionDetailEntityError(
        val title: String,
        val message: String,
        val bottomMessage: String,
        val promotionEntityList: List<PromotionEntity>
    )

    private fun JSONObject.parsePromotionDetailEntity() = PromotionDetailEntity(
        id = getJSONObject("aktion").getLong("ID"),
        name = getJSONObject("aktion").getString("NAME"),
        detailText = getJSONObject("aktion").getString("DETAIL_TEXT"),
        detailPicture = getJSONObject("aktion").getString("DETAIL_PICTURE"),
        status = getJSONObject("aktion").getString("NAMERAZDEL"),
        statusColor = getJSONObject("aktion").getString("CVET"),
        timeLeft = getJSONObject("aktion").getString("DATAOUT"),
        promotionCategoryDetailEntity = when(isNull("production")) {
            false -> CategoryDetailEntity(
                name = getJSONObject("production").getString("NAMETOVAR"),
                productEntityList = getJSONObject("production").getJSONArray("TOVARY").parseProductEntityList()
            )
            true -> null
        },
        forYouCategoryDetailEntity = CategoryDetailEntity(
            name = getString("title"),
            productEntityList = getJSONArray("tovary").parseProductEntityList()
        )
    )

    private fun JSONObject.parsePromotionDetailErrorEntity() = PromotionDetailEntityError(
        title = safeString("title"),
        message = safeString("message"),
        bottomMessage = when(isNull("tovarpopyl")) {
            false -> getJSONObject("tovarpopyl").safeString("NAME")
            true -> ""
        },
        promotionEntityList = when(isNull("tovarpopyl")) {
            false -> {
                getJSONObject("tovarpopyl").getJSONArray("data").parsePromotionEntityList()
            }
            true -> emptyList<PromotionEntity>()
        }
    )

    private fun JSONArray.parsePromotionEntityList(): List<PromotionEntity> = mutableListOf<PromotionEntity>().apply {
        for (index in 0 until length()) {
            add(getJSONObject(index).parsePromotionEntity())
        }
    }

    private fun JSONObject.parsePromotionEntity() = PromotionEntity(
        id = getString("ID").toLong(),
        name = getString("NAME"),
        detailPicture = getString("PREVIEW_PICTURE").parseImagePath(),
        timeLeft = getString("DATAOUT"),
        statusColor = getString("CVET"),
        customerCategory = getString("NAMERAZDEL")
    )

}