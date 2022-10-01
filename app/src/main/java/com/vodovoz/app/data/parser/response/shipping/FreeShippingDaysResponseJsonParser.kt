package com.vodovoz.app.data.parser.response.shipping

import com.vodovoz.app.data.model.common.FreeShippingDaysInfoBundleEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import okhttp3.ResponseBody
import org.json.JSONObject

object FreeShippingDaysResponseJsonParser {

    fun ResponseBody.parseFreeShippingDaysResponse(): ResponseEntity<FreeShippingDaysInfoBundleEntity> {
        val responseJson = JSONObject(this.string())
        return ResponseEntity.Success(FreeShippingDaysInfoBundleEntity(
            title = responseJson.getJSONObject("dostavka_informat").getString("NAME"),
            info = responseJson.getJSONObject("dostavka_informat").getString("OPISANIE"),
        ))
    }

}