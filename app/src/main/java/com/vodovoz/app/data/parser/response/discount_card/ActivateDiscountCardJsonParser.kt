package com.vodovoz.app.data.parser.response.discount_card

import android.util.Log
import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.DiscountCardPropertyEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.features.ActivateDiscountCardBundleEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.util.LogSettings
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object ActivateDiscountCardJsonParser {

    fun ResponseBody.parseActivateDiscountCardResponse(): ResponseEntity<String> {
        val responseJson = JSONObject(string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getString("data"))
            else -> ResponseEntity.Error(responseJson.getString("data"))
        }
    }

}