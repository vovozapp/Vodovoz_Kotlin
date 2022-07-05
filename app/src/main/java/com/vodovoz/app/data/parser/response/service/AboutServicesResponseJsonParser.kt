package com.vodovoz.app.data.parser.response.service

import com.vodovoz.app.data.model.common.AboutServicesBundleEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.ServiceEntity
import com.vodovoz.app.data.model.features.AllPromotionsBundleEntity
import com.vodovoz.app.data.parser.common.PromotionJsonParser.parsePromotionEntityList
import com.vodovoz.app.data.parser.common.PromotionParser.parsePromotionFilterEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object AboutServicesResponseJsonParser {

    fun ResponseBody.parseAboutServicesResponse(): ResponseEntity<AboutServicesBundleEntity> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getJSONObject("data").parseAboutServicesBundleEntity())
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONObject.parseAboutServicesBundleEntity() = AboutServicesBundleEntity(
        title = getString("TITLE"),
        detail = getString("NAME"),
        serviceEntityList = getJSONArray("OPIS").parseServiceEntityList()
    )

    private fun JSONArray.parseServiceEntityList(): List<ServiceEntity> = mutableListOf<ServiceEntity>().also { list ->
        for (index in 0 until length()) {
            list.add(getJSONObject(index).parseServiceEntity())
        }
    }
    private fun JSONObject.parseServiceEntity() = ServiceEntity(
        name = getString("TITLE"),
        detail = getString("TEXT"),
        type = getString("TIP")
    )
}