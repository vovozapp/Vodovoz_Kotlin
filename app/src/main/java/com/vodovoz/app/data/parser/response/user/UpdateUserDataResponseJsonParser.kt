package com.vodovoz.app.data.parser.response.user

import android.util.Log
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.UserDataEntity
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import com.vodovoz.app.util.LogSettings
import okhttp3.ResponseBody
import org.json.JSONObject

object UpdateUserDataResponseJsonParser {

    fun ResponseBody.parseUpdateUserDataResponse(): ResponseEntity<Boolean> {
        val responseJson = JSONObject(this.string())
        Log.i(LogSettings.ID_LOG, responseJson.toString())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(true)
            else -> ResponseEntity.Error(responseJson.getString("data"))
        }
    }

}