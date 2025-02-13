package com.vodovoz.app.data.parser.response.map

import com.vodovoz.app.data.model.common.AddressEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object FetchAddressesSavedResponseJsonParser {

    fun ResponseBody.parseFetchAddressesSavedResponse(): ResponseEntity<List<AddressEntity>> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                responseJson.getJSONArray("data").parseAddressEntityList()
            )
            else ->  ResponseEntity.Success(listOf())
        }
    }

    private fun JSONArray.parseAddressEntityList(): List<AddressEntity> = mutableListOf<AddressEntity>().also { list ->
        for (index in 0 until length()) {
            list.add(getJSONObject(index).parseAddressEntity())
        }
    }

    private fun JSONObject.parseAddressEntity(): AddressEntity {
        val propsList = getJSONArray("PROPS").parseAddressPropertyList()
        return AddressEntity(
            id = getString("ID").toLong(),
            fullAddress = getString("NAME"),
            inn = propsList.find { it.first == "INN" }?.second ?: "",
            companyName = propsList.find { it.first == "COMPANY" }?.second ?: "",
            type = getString("PERSON_TYPE_ID").toInt(),
            phone = propsList.find { it.first == "PHONE" }?.second ?: "",
            name = propsList.find { it.first == "FIO" || it.first == "CONTACT_PERSON"}?.second ?: "",
            email = propsList.find { it.first == "EMAIL" }?.second ?: "",
            locality = propsList.find { it.first == "CITY" }?.second ?: "",
            street = propsList.find { it.first == "ADDRESS" }?.second ?: "",
            house = propsList.find { it.first == "DOM" || it.first == "F_DOM" }?.second ?: "",
            entrance = propsList.find { it.first == "POD" || it.first == "F_POD"  }?.second ?: "",
            floor = propsList.find { it.first == "ETAJ" || it.first == "F_ETAJ"  }?.second ?: "",
            flat = propsList.find { it.first == "KV" || it.first == "F_OFIS" }?.second ?: "",
            intercom = propsList.find { it.first == "domofon"}?.second ?: "",
            length = propsList.find {it.first == "UF_SEMAP_LEN_KM"}?.second ?: "",
            coordinates = propsList.find {it.first == "KOORDINAT_TOCHKI"}?.second ?: "",
            newFullAddress = propsList.find { it.first == "ADDRESS_DELIVERY" }?.second ?: ""
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