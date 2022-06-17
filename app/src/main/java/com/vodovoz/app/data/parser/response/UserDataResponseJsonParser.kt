package com.vodovoz.app.data.parser.response

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.UserDataEntity
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import okhttp3.ResponseBody
import org.json.JSONObject

object UserDataResponseJsonParser {

    fun ResponseBody.parseUserDataResponse(): ResponseEntity<UserDataEntity> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getJSONObject("data").parseUserDataEntity())
            else -> ResponseEntity.Error(responseJson.getString("message"))
        }
    }

    private fun JSONObject.parseUserDataEntity() = UserDataEntity(
        id = getLong("ID"),
        firstName = getString("NAME"),
        secondName = getString("LAST_NAME"),
        email = getString("EMAIL"),
        registerDate = getString("DATE_REGISTER"),
        avatar = getString("PERSONAL_PHOTO").parseImagePath(),
        phone = getString("PERSONAL_PHONE"),
        birthday = getString("PERSONAL_BIRTHDAY"),
        token = getString("UF_TOKEN")
    )

}