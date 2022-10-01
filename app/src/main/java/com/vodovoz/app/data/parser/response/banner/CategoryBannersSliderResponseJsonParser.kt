package com.vodovoz.app.data.parser.response.banner

import com.vodovoz.app.data.model.common.BannerEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.BannerJsonParser.parseBannerEntityList
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object CategoryBannersSliderResponseJsonParser {

    fun ResponseBody.parseCategoryBannersSliderResponse(): ResponseEntity<List<BannerEntity>> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getJSONArray("data").parseBannerEntityList())
            else -> ResponseEntity.Error("Ошибка парсинга баннеры категорий")
        }
    }

}