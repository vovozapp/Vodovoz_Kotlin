package com.vodovoz.app.data.parser.response.shipping

import com.vodovoz.app.data.model.common.AboutServicesBundleEntity
import com.vodovoz.app.data.model.common.FreeShippingDaysInfoBundleEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.ServiceEntity
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
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