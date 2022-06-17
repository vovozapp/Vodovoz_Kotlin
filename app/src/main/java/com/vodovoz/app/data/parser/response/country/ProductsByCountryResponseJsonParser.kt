package com.vodovoz.app.data.parser.response.country

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.features.CountryBundleEntity
import com.vodovoz.app.data.parser.common.ProductJsonParser.parseProductEntityList
import com.vodovoz.app.data.parser.response.country.CountrySliderResponseJsonParser.parseCountryEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import okhttp3.ResponseBody
import org.json.JSONObject

object ProductsByCountryResponseJsonParser {

    fun ResponseBody.parseProductsByCountryResponse(): ResponseEntity<List<ProductEntity>> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS ->
                ResponseEntity.Success(responseJson.getJSONArray("data").parseProductEntityList())
            else -> ResponseEntity.Error()
        }
    }

}