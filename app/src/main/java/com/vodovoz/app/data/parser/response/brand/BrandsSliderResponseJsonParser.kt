package com.vodovoz.app.data.parser.response.brand

import com.vodovoz.app.data.model.common.BrandsMainEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.BrandJsonParser.parseBrandEntityList
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object BrandsSliderResponseJsonParser {

    fun ResponseBody.parseBrandsSliderResponse(): ResponseEntity<BrandsMainEntity> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                BrandsMainEntity(
                    name = responseJson.safeString("glavtitle"),
                    brandsList = responseJson.getJSONArray("data").parseBrandEntityList()
                )
            )

            else -> ResponseEntity.Error("Ошибка парсинга бренды")
        }
    }

}