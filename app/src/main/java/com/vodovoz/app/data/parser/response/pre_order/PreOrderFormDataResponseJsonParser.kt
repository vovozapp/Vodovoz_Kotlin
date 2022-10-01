package com.vodovoz.app.data.parser.response.pre_order

import com.vodovoz.app.data.model.common.PreOrderFormDataEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object PreOrderFormDataResponseJsonParser {

    fun ResponseBody.parsePreOrderFormDataResponse(): ResponseEntity<PreOrderFormDataEntity> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(PreOrderFormDataEntity(
                name = responseJson.safeString("FIO"),
                email = responseJson.safeString("EMAIL"),
                phone = responseJson.safeString("TELEFON")
            ))
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

}