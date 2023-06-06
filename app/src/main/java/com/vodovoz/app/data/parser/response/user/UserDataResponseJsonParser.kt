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
        firstName = safeString("NAME"),
        secondName = safeString("LAST_NAME"),
        sex = when(safeString("PERSONAL_GENDER")) {
            "M" -> "Мужской"
            "F" -> "Женский"
            else -> "Не указано"
        },
        email = safeString("EMAIL"),
        registerDate = safeString("DATE_REGISTER"),
        avatar = safeString("PERSONAL_PHOTO").parseImagePath(),
        phone = safeString("PERSONAL_PHONE"),
        birthday = when(isNull("PERSONAL_BIRTHDAY")) {
            true -> "Не указано"
            false -> safeString("PERSONAL_BIRTHDAY")
        },
        token = safeString("UF_TOKEN")
    )

}