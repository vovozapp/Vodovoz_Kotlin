package com.vodovoz.app.data.parser.response.ordering

import com.vodovoz.app.data.model.common.*
import com.vodovoz.app.data.parser.common.safeLong
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object RegOrderResponseJsonParser {

    fun ResponseBody.parseRegOrderResponse(): ResponseEntity<OrderingCompletedInfoBundleEntity> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(OrderingCompletedInfoBundleEntity(
                orderId = responseJson.safeLong("data"),
                message = responseJson.safeString("message"),
                paymentURL = responseJson.getJSONObject("paysystem").safeString("URL")
            ))
            else -> ResponseEntity.Error(responseJson.getString("message"))
        }
    }

}