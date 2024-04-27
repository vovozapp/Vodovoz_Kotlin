package com.vodovoz.app.data.parser.response.rate

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.safeInt
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.feature.home.ratebottom.model.ProductRateBottom
import com.vodovoz.app.feature.home.ratebottom.model.RateBottomData
import com.vodovoz.app.feature.home.ratebottom.model.RateBottomModel
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object RateBottomModelJsonParser {

    fun ResponseBody.parseRateBottomModel(): ResponseEntity<RateBottomModel> {
        val responseJson = JSONObject(this.string())
        return ResponseEntity.Success(
            RateBottomModel(
                status = responseJson.safeString("status"),
                message = responseJson.getString("message"),
                rateBottomData = if (responseJson.has("data")
                    && !responseJson.isNull("data")
                    && responseJson.safeString("status") != "Error"
                ) {
                    responseJson.getJSONObject("data").parseRateBottomData()
                } else {
                    null
                }
            )
        )
    }

    private fun JSONObject.parseRateBottomData() = RateBottomData(
        productsList = if (has("LISTRAZDEL")) {
            getJSONArray("LISTRAZDEL").parseProductsList()
        } else {
            null
        },
        titleCategory = safeString("TITLERAZDEL"),
        titleProduct = safeString("TITLETOVAR"),
        allProductsCount = safeString("VSEGOTOVAR")
    )

    private fun JSONArray.parseProductsList() =  mutableListOf<ProductRateBottom>().apply {
        for (index in 0 until length()) {
            add(getJSONObject(index).parseProductRateBottom())
        }
    }

    private fun JSONObject.parseProductRateBottom() = ProductRateBottom (
        image = safeString("DETAIL_PICTURE"),
        id = safeInt("ID"),
        name = safeString("NAME")
    )



}