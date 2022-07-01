package com.vodovoz.app.data.parser.response.catalog

import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.CategoryJsonParser.parseCategoryEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object CatalogResponseJsonParser {

    fun ResponseBody.parseCatalogResponse(): ResponseEntity<List<CategoryEntity>> {
        val responseJson = JSONObject(string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getJSONArray("data").parseCategoryEntityList())
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

}