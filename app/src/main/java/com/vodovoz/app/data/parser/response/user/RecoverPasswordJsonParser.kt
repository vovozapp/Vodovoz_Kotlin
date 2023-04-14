package com.vodovoz.app.data.parser.response.user

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.util.extensions.debugLog
import okhttp3.ResponseBody
import org.json.JSONObject

object RecoverPasswordJsonParser {

    fun ResponseBody.parseRecoverPasswordResponse(): ResponseEntity<Boolean> {
        return try {
            val responseJson = JSONObject(this.string())
            return when(responseJson.getString("status")) {
                ResponseStatus.SUCCESS -> ResponseEntity.Success(true)
                else -> ResponseEntity.Error(responseJson.getString("message"))
            }
        } catch (t: Throwable) {
            ResponseEntity.Error("Ошибка. Попробуйте снова.")
        }

    }

}