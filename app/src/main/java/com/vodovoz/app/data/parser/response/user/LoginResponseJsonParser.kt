package com.vodovoz.app.data.parser.response.user

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.UserAuthDataEntity
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONObject

object LoginResponseJsonParser {

    fun ResponseBody.parseLoginResponse(): ResponseEntity<UserAuthDataEntity> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                UserAuthDataEntity (
                    id = responseJson.getJSONObject("data").getLong("user_id"),
                    token = responseJson.safeString("token")
                )
            )
            else -> ResponseEntity.Error(responseJson.getString("message"))
        }
    }

}