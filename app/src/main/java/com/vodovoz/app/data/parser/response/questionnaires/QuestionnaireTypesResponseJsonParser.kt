package com.vodovoz.app.data.parser.response.questionnaires

import com.vodovoz.app.data.model.common.QuestionnaireTypeEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object QuestionnaireTypesResponseJsonParser {

    fun ResponseBody.parseQuestionnaireTypesResponse(): ResponseEntity<List<QuestionnaireTypeEntity>> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getJSONArray("data").parseQuestionnaireTypeList())
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONArray.parseQuestionnaireTypeList(): List<QuestionnaireTypeEntity> =
        mutableListOf<QuestionnaireTypeEntity>().also { list ->
        for (index in 0 until length()) list.add(getJSONObject(index).parseQuestionnaireType())
    }
    private fun JSONObject.parseQuestionnaireType() = QuestionnaireTypeEntity(
        name = safeString("NAME"),
        type = safeString("URL")
    )
}