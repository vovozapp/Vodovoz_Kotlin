package com.vodovoz.app.data.parser.response.map

import android.util.Log
import com.vodovoz.app.data.model.common.*
import com.vodovoz.app.data.model.features.DeliveryZonesBundleEntity
import com.vodovoz.app.data.remote.ResponseStatus
import com.vodovoz.app.util.LogSettings
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object FetchAddressesSavedResponseJsonParser {

    fun ResponseBody.parseFetchAddressesSavedResponse(): ResponseEntity<List<AddressEntity>> {
        val responseJson = JSONObject(string())
        Log.i(LogSettings.ID_LOG, responseJson.toString())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                responseJson.getJSONArray("data").parseAddressEntityList().apply {
                    Log.i(LogSettings.ID_LOG, "SUCCESS PARSE")
                }
            )
            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONArray.parseAddressEntityList(): List<AddressEntity> = mutableListOf<AddressEntity>().also { list ->
        Log.i(LogSettings.ID_LOG, "PRE PARSE")
        for (index in 0 until length()) {
            Log.i(LogSettings.ID_LOG, index.toString())
            list.add(getJSONObject(index).parseAddressEntity())
        }
    }

    private fun JSONObject.parseAddressEntity(): AddressEntity {
        val propsList = getJSONArray("PROPS").parseAddressPropertyList()
        return AddressEntity(
            id = getString("ID").toLong(),
            fullAddress = getString("NAME"),
            type = getString("PERSON_TYPE_ID").toInt(),
            phone = propsList.find { it.first == "PHONE" }?.second ?: "",
            name = propsList.find { it.first == "FIO" }?.second?: "",
            email = propsList.find { it.first == "EMAIL" }?.second?: "",
            locality = propsList.find { it.first == "CITY" }?.second?: "",
            street = propsList.find { it.first == "ADDRESS" }?.second?: "",
            house = propsList.find { it.first == "DOM" || it.first == "F_DOM" }?.second?: "",
            entrance = propsList.find { it.first == "POD" || it.first == "F_POD"  }?.second?: "",
            floor = propsList.find { it.first == "ETAJ" || it.first == "F_ETAJ"  }?.second?: "",
            flat = propsList.find { it.first == "KV" || it.first == "F_OFIS" }?.second?: "",
            comment = propsList.find { it.first == "KOMENT" || it.first == "F_KOMENT" }?.second?: ""
        )
    }

    private fun JSONArray.parseAddressPropertyList(): List<Pair<String, String>> = mutableListOf<Pair<String, String>>().also { list ->
        for (index in 0 until length()) {
            list.add(Pair(
                getJSONObject(index).getString("CODE"),
                getJSONObject(index).getString("VALUE")
            ))
        }
    }


}