package com.vodovoz.app.data.parser.response.brand

import com.vodovoz.app.data.model.common.BrandEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.BrandJsonParser.parseBrandEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object BrandsSliderResponseJsonParser {

    fun ResponseBody.parseBrandsSliderResponse(): ResponseEntity<List<BrandEntity>> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getJSONArray("data").parseBrandEntityList())
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

}