package com.vodovoz.app.data.parser.response.country

import com.vodovoz.app.data.model.common.CountryEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.features.CountriesSliderBundleEntity
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object CountrySliderResponseJsonParser {

    fun ResponseBody.parseCountriesSliderResponse(): ResponseEntity<CountriesSliderBundleEntity> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                CountriesSliderBundleEntity(
                    title = responseJson.getJSONObject("data").getString("TITLE"),
                    backgroundPicture = responseJson.getJSONObject("data")
                        .getString("BACGROUND").parseImagePath(),
                    countryEntityList = responseJson.getJSONObject("data")
                        .getJSONArray("STRANY").parseCountryEntityList()
                )
            )
            else -> ResponseEntity.Error("Ошибка парсинга страны")
        }
    }

    fun JSONArray.parseCountryEntityList(): List<CountryEntity> = mutableListOf<CountryEntity>().apply {
        for (index in 0 until length()) {
            add(getJSONObject(index).parseCountryEntity())
        }
    }

    fun JSONObject.parseCountryEntity() = CountryEntity(
        id = getString("ID").toLong(),
        name = getString("NAME"),
        detailPicture = getString("PREVIEW_PICTURE").parseImagePath()
    )

}