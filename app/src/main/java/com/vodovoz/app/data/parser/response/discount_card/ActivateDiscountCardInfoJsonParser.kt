package com.vodovoz.app.data.parser.response.discount_card

import com.vodovoz.app.data.model.common.DiscountCardPropertyEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.features.ActivateDiscountCardBundleEntity
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object ActivateDiscountCardInfoJsonParser {

    fun ResponseBody.parseActivateDiscountCardInfoResponse(): ResponseEntity<ActivateDiscountCardBundleEntity> {
        val responseJson = JSONObject(string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(ActivateDiscountCardBundleEntity(
                title = responseJson.getString("titlle"),
                details = responseJson.getString("opisanie"),
                discountCardPropertyEntityList = responseJson.getJSONArray("data").parseDiscountCardPropertyEntityList()
            ))
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONArray.parseDiscountCardPropertyEntityList(): List<DiscountCardPropertyEntity> = mutableListOf<DiscountCardPropertyEntity>().also { list ->
        for (index in 0 until length()) {
            list.add(getJSONObject(index).parseDiscountCardPropertyEntity())
        }
    }

    private fun JSONObject.parseDiscountCardPropertyEntity() = DiscountCardPropertyEntity(
        id = getLong("ID"),
        name = getString("NAME"),
        code = getString("CODE"),
        value = getString("VALUE")
    )

}