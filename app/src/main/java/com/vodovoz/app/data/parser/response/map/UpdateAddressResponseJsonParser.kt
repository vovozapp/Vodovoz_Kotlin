package com.vodovoz.app.data.parser.response.map

import android.util.Log
import com.vodovoz.app.data.model.common.DeliveryZoneEntity
import com.vodovoz.app.data.model.common.HistoryEntity
import com.vodovoz.app.data.model.common.PointEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.features.DeliveryZonesBundleEntity
import com.vodovoz.app.data.parser.common.HistoryJsonParser.parseHistoryEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.util.LogSettings
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object UpdateAddressResponseJsonParser {

    fun ResponseBody.parseUpdateAddressResponse(): ResponseEntity<String> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getString("message"))
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

}