package com.vodovoz.app.data.parser.response.contacts

import com.vodovoz.app.data.model.common.ChatEntity
import com.vodovoz.app.data.model.common.ChatsBundleEntity
import com.vodovoz.app.data.model.common.ContactsBundleEntity
import com.vodovoz.app.data.model.common.EmailEntity
import com.vodovoz.app.data.model.common.PhoneEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object ContactsBundleResponseJsonParser {

    fun ResponseBody.parseContactsBundleResponse(): ResponseEntity<ContactsBundleEntity> {
        val responseJson = JSONObject(string())
        return when(responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(responseJson.parseContactsBundleEntity())
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }


    private fun JSONObject.parseContactsBundleEntity() = ContactsBundleEntity(
        title = getString("title"),
        phoneEntityList = getJSONObject("telefon").getJSONArray("DANNIE").parsePhoneEntityList(),
        emailEntityList = getJSONObject("pochata").getJSONArray("DANNIE").parseEmailEntityList(),
        chatsBundleEntity = getJSONObject("telefon").getJSONArray("DANNIE").parseChatsBundleEntity()
    )

    private fun JSONArray.parseChatsBundleEntity(): ChatsBundleEntity? {
        for (index in 0 until length()) {
            if (getJSONObject(index).has("DANNYECHAT")) {
                return getJSONObject(index).parseChatsBundleEntity()
            }
        }
        return null
    }

    private fun JSONObject.parseChatsBundleEntity() = ChatsBundleEntity(
        name = getString("NAME"),
        chatEntityList = getJSONArray("DANNYECHAT").parseChatEntityList()
    )

    private fun JSONArray.parseChatEntityList(): List<ChatEntity> = mutableListOf<ChatEntity>().also { list ->
        for (index in 0 until length()) list.add(getJSONObject(index).parseChatEntity())
    }

    private fun JSONObject.parseChatEntity() = ChatEntity(
        icon = getString("NAME").parseImagePath(),
        action = getString("CHATDAN"),
        type = getString("ID")
    )

    private fun JSONArray.parseEmailEntityList(): List<EmailEntity> = mutableListOf<EmailEntity>().also { list ->
        for (index in 0 until length()) list.add(getJSONObject(index).parseEmailEntity())
    }

    private fun JSONObject.parseEmailEntity() = EmailEntity(
        name = getString("NAME"),
        value = getString("DANNYE"),
        type = getString("ID")
    )

    private fun JSONArray.parsePhoneEntityList(): List<PhoneEntity> = mutableListOf<PhoneEntity>().also { list ->
        for (index in 0 until length()) {
            if (getJSONObject(index).has("DANNYECHAT")) continue
            list.add(getJSONObject(index).parsePhoneEntity())
        }
    }

    private fun JSONObject.parsePhoneEntity() = PhoneEntity(
        name = getString("NAME"),
        value = getString("DANNYE"),
        type = getString("ID")
    )

}