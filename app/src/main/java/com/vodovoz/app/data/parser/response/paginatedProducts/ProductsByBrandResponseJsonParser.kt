package com.vodovoz.app.data.parser.response.paginatedProducts

import android.util.Log
import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.features.CountryBundleEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.parser.response.country.CountrySliderResponseJsonParser.parseCountryEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import com.vodovoz.app.util.LogSettings
import okhttp3.ResponseBody
import org.json.JSONObject

object ProductsByBrandResponseJsonParser {

    fun ResponseBody.parseProductsByBrandResponse(): ResponseEntity<List<ProductEntity>> {
        val responseJson = JSONObject(string())
        Log.i(LogSettings.REQ_RES_LOG, responseJson.toString())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                when(responseJson.isNull("tovar")) {
                    true -> listOf()
                    false -> responseJson.getJSONArray("tovar").parseProductEntityList()
                }
            )
            else -> ResponseEntity.Success(listOf())
        }
    }

}