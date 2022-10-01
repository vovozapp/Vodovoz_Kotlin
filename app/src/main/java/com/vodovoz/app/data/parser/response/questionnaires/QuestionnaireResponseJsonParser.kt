package com.vodovoz.app.data.parser.response.questionnaires

import com.vodovoz.app.data.model.common.LinkEntity
import com.vodovoz.app.data.model.common.QuestionEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object QuestionnaireResponseJsonParser {

    fun ResponseBody.parseQuestionnaireResponse(): ResponseEntity<List<QuestionEntity>> {
        val responseJson = JSONObject(this.string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.getJSONArray("data").parseQuestionEntityList())
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONArray.parseQuestionEntityList(): List<QuestionEntity> = mutableListOf<QuestionEntity>().also { list ->
        for (index in 0 until length()) list.add(getJSONObject(index).parseQuestion())
    }

    private fun JSONObject.parseQuestion() = when(getString("PROPERTY_TYPE")) {
        "L" -> when(getString("MULTIPLE")) {
            "Y" -> parseMultiAnswerQuestion()
            else -> parseSingleAnswerQuestion()
        }
        else -> parseInputAnswerQuestion()
    }

    private fun JSONObject.parseInputAnswerQuestion() = QuestionEntity.InputAnswer(
        name = getString("NAME"),
        code = getString("CODE"),
        isRequired = when(getString("IS_REQUIRED")) {
            "Y" -> true
            else -> false
        },
        defaultAnswer = when(has("VALUE")) {
            true -> getString("VALUE")
            false -> ""
        }
    )

    private fun JSONObject.parseMultiAnswerQuestion() = QuestionEntity.MultiAnswer(
        name = getString("NAME"),
        code = getString("CODE"),
        isRequired = when(getString("IS_REQUIRED")) {
            "Y" -> true
            else -> false
        },
        answerList = getJSONArray("RAZDEL").parseAnswerList()
    )

    private fun JSONObject.parseSingleAnswerQuestion() = QuestionEntity.SingleAnswer(
        name = getString("NAME"),
        code = getString("CODE"),
        isRequired = when(getString("IS_REQUIRED")) {
            "Y" -> true
            else -> false
        },
        answerList = getJSONArray("RAZDEL").parseAnswerList(),
        linkList = when(has("USLOVIE")) {
            false -> listOf()
            true -> getJSONArray("USLOVIE").parseLinkEntityList()
        }
    )

    private fun JSONArray.parseLinkEntityList(): List<LinkEntity> = mutableListOf<LinkEntity>().also { list ->
        for (index in 0 until length()) list.add(getJSONObject(index).parseLinkEntity())
    }

    private fun JSONObject.parseLinkEntity() = LinkEntity(
        name = getString("TEXT"),
        link = getString("URL")
    )

    private fun JSONArray.parseAnswerList(): List<String> = mutableListOf<String>().also { list ->
        for (index in 0 until length()) {
            list.add(getString(index))
        }
    }

}