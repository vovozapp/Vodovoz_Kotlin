package com.vodovoz.app.data.parser.response.service

import com.vodovoz.app.data.model.common.AboutServicesBundleEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.ServiceEntity
import com.vodovoz.app.data.model.features.AllPromotionsBundleEntity
import com.vodovoz.app.data.parser.common.PromotionJsonParser.parsePromotionEntityList
import com.vodovoz.app.data.parser.common.PromotionParser.parsePromotionFilterEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object OrderServiceResponseJsonParser {

    fun ResponseBody.parseOrderServiceResponse(): ResponseEntity<String> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getString("data"))
            else -> ResponseEntity.Error(responseJson.getString("data"))
        }
    }

}