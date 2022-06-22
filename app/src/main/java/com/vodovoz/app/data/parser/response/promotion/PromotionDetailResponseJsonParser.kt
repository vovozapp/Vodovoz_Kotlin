package com.vodovoz.app.data.parser.response.promotion

import android.util.Log
import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.PromotionDetailEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.util.LogSettings
import okhttp3.ResponseBody
import org.json.JSONObject

object PromotionDetailResponseJsonParser {

    fun ResponseBody.parsePromotionDetailResponse(): ResponseEntity<PromotionDetailEntity> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.parsePromotionDetailEntity())
            else -> ResponseEntity.Error(responseJson.getString("message"))
        }
    }

    private fun JSONObject.parsePromotionDetailEntity() = PromotionDetailEntity(
        id = getJSONObject("aktion").getLong("ID"),
        name = getJSONObject("aktion").getString("NAME"),
        detailText = getJSONObject("aktion").getString("DETAIL_TEXT"),
        detailPicture = getJSONObject("aktion").getString("DETAIL_PICTURE"),
        status = getJSONObject("aktion").getString("NAMERAZDEL"),
        statusColor = getJSONObject("aktion").getString("CVET"),
        timeLeft = getJSONObject("aktion").getString("DATAOUT"),
        promotionCategoryDetailEntity = CategoryDetailEntity(
            name = getJSONObject("production").getString("NAMETOVAR"),
            productEntityList = getJSONObject("production").getJSONArray("TOVARY").parseProductEntityList()
        ),
        forYouCategoryDetailEntity = CategoryDetailEntity(
            name = getString("title"),
            productEntityList = getJSONArray("tovary").parseProductEntityList()
        )
    )

}