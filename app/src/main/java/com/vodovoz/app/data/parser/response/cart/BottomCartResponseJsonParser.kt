package com.vodovoz.app.data.parser.response.cart

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.features.BottomCartEntity
import com.vodovoz.app.data.parser.common.safeDouble
import com.vodovoz.app.data.parser.common.safeInt
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object BottomCartResponseJsonParser {

    fun ResponseBody.parseBottomCartResponse(): ResponseEntity<BottomCartEntity> {
        val responseJson = JSONObject(this.string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                responseJson.getJSONArray("data").parseBottomCartEntityList().first()
            )

            else -> ResponseEntity.Error(responseJson.getString("message"))
        }
    }

    private fun JSONArray.parseBottomCartEntityList(): List<BottomCartEntity> =
        mutableListOf<BottomCartEntity>().also { list ->
            for (index in 0 until length()) list.add(getJSONObject(index).parseBottomCartEntity())
        }

    private fun JSONObject.parseBottomCartEntity() = BottomCartEntity(
        totalSum = safeDouble("ALLSUMA").toFloat(),
        quantity = safeInt("QUANTITY"),
        productCount = safeInt("TOVAROV"),
    )
}