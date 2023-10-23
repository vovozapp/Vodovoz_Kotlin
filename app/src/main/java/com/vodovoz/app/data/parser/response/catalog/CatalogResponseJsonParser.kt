package com.vodovoz.app.data.parser.response.catalog

import com.vodovoz.app.data.model.common.CatalogEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.CategoryJsonParser.parseCategoryBanner
import com.vodovoz.app.data.parser.common.CategoryJsonParser.parseCategoryEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object CatalogResponseJsonParser {

    fun ResponseBody.parseCatalogResponse(): ResponseEntity<CatalogEntity> {
        val responseJson = JSONObject(string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                CatalogEntity(
                    categoryEntityList = responseJson.getJSONArray("data").parseCategoryEntityList(),
                    topCatalogBanner = if(responseJson.has("knopka")){
                        responseJson.getJSONObject("knopka").parseCategoryBanner()
                    } else {
                        null
                    }
                )
            )
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

}