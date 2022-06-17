package com.vodovoz.app.data.parser.response.promotion

import com.vodovoz.app.data.model.common.PromotionEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.parser.common.PromotionJsonParser.parsePromotionEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object PromotionSliderResponseJsonParser {

    fun ResponseBody.parsePromotionSliderResponse(): ResponseEntity<List<PromotionEntity>> {
        val responseJson = JSONObject(string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                responseJson.getJSONArray("data").parsePromotionEntityList()
            )
            else -> ResponseEntity.Error()
        }
    }

}