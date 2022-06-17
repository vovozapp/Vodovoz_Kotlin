package com.vodovoz.app.data.parser.response.promotion

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.features.AllPromotionBundleEntity
import com.vodovoz.app.data.parser.common.PromotionJsonParser.parsePromotionEntityList
import com.vodovoz.app.data.parser.common.PromotionParser.parsePromotionFilterEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object AllPromotionsResponseJsonParser {

    fun ResponseBody.parseAllPromotionsResponse(): ResponseEntity<AllPromotionBundleEntity> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(AllPromotionBundleEntity(
                promotionEntityList = responseJson.getJSONArray("data").parsePromotionEntityList(),
                promotionFilterEntityList = responseJson.getJSONArray("namerazdely").parsePromotionFilterEntityList()
            ))
            else -> ResponseEntity.Error()
        }
    }


}