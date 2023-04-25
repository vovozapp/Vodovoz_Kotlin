package com.vodovoz.app.data.parser.response.user

import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.UserDataEntity
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import okhttp3.ResponseBody
import org.json.JSONObject

object UserDataResponseJsonParser {

    fun ResponseBody.parseUserDataResponse(): ResponseEntity<UserDataEntity> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getJSONObject("data").parseUserDataEntity())
            else -> ResponseEntity.Error(responseJson.safeString("message"))
        }
    }

    private fun JSONObject.parseUserDataEntity() = UserDataEntity(
        id = getLong("ID"),
        firstName = getString("NAME"),
        secondName = getString("LAST_NAME"),
        sex = when(getString("PERSONAL_GENDER")) {
            "M" -> "Мужской"
            "F" -> "Женский"
            else -> "Не указано"
        },
        email = getString("EMAIL"),
        registerDate = getString("DATE_REGISTER"),
        avatar = getString("PERSONAL_PHOTO").parseImagePath(),
        phone = getString("PERSONAL_PHONE"),
        birthday = when(isNull("PERSONAL_BIRTHDAY")) {
            true -> "Не указано"
            false -> getString("PERSONAL_BIRTHDAY")
        },
        token = getString("UF_TOKEN")
    )

}