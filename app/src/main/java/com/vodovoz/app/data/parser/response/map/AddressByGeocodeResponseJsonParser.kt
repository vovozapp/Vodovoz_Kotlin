package com.vodovoz.app.data.parser.response.map

import android.util.Log
import com.vodovoz.app.data.model.common.*
import com.vodovoz.app.util.LogSettings
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object AddressByGeocodeResponseJsonParser {

    fun ResponseBody.parseAddressByGeocodeResponse(): ResponseEntity<AddressEntity> {
        val responseJson = JSONObject(string())
        Log.i(LogSettings.ID_LOG, "0")
        return ResponseEntity.Success(responseJson.getJSONObject("response").parseAddressEntity())
    }

    private fun JSONObject.parseAddressEntity(): AddressEntity {
        if (has("GeoObjectCollection")) {
            Log.i(LogSettings.ID_LOG, "1")
            if (getJSONObject("GeoObjectCollection").has("featureMember")) {
                Log.i(LogSettings.ID_LOG, "2")
                if (getJSONObject("GeoObjectCollection").getJSONArray("featureMember").length() != 0) {
                    Log.i(LogSettings.ID_LOG, "3")
                    if (getJSONObject("GeoObjectCollection")
                            .getJSONArray("featureMember")
                            .getJSONObject(0)
                            .has("GeoObject")
                    ) {
                        Log.i(LogSettings.ID_LOG, "4")
                        if (getJSONObject("GeoObjectCollection")
                                .getJSONArray("featureMember")
                                .getJSONObject(0)
                                .getJSONObject("GeoObject")
                                .getJSONObject("metaDataProperty")
                                .has("GeocoderMetaData")
                        ) {
                            Log.i(LogSettings.ID_LOG, "5")
                            if (getJSONObject("GeoObjectCollection")
                                    .getJSONArray("featureMember")
                                    .getJSONObject(0)
                                    .getJSONObject("GeoObject")
                                    .getJSONObject("metaDataProperty")
                                    .getJSONObject("GeocoderMetaData")
                                    .has("Address")
                            ) {
                                Log.i(LogSettings.ID_LOG, "6")
                                if (getJSONObject("GeoObjectCollection")
                                        .getJSONArray("featureMember")
                                        .getJSONObject(0)
                                        .getJSONObject("GeoObject")
                                        .getJSONObject("metaDataProperty")
                                        .getJSONObject("GeocoderMetaData")
                                        .getJSONObject("Address")
                                        .has("Components")
                                ) {
                                    Log.i(LogSettings.ID_LOG, "7")
                                    val addressComponentList = getJSONObject("GeoObjectCollection")
                                        .getJSONArray("featureMember")
                                        .getJSONObject(0)
                                        .getJSONObject("GeoObject")
                                        .getJSONObject("metaDataProperty")
                                        .getJSONObject("GeocoderMetaData")
                                        .getJSONObject("Address")
                                        .getJSONArray("Components")
                                        .parseAddressComponentList()

                                    val fullAddress = getJSONObject("GeoObjectCollection")
                                        .getJSONArray("featureMember")
                                        .getJSONObject(0)
                                        .getJSONObject("GeoObject")
                                        .getJSONObject("metaDataProperty")
                                        .getJSONObject("GeocoderMetaData")
                                        .getString("text")

                                    Log.i(LogSettings.ID_LOG, "8")
                                    return AddressEntity(
                                        fullAddress = fullAddress,
                                        locality = addressComponentList.find { it.first == "locality" }?.second,
                                        street = addressComponentList.find { it.first == "street" }?.second,
                                        house = addressComponentList.find { it.first == "house" }?.second
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        return AddressEntity()
    }
    private fun JSONArray.parseAddressComponentList(): List<Pair<String, String>> = mutableListOf<Pair<String, String>>().also { list ->
        for (index in 0 until length()) {
            list.add(getJSONObject(index).parseAddressComponent())
        }
    }

    private fun JSONObject.parseAddressComponent() = Pair(
        getString("kind"),
        getString("name")
    )

}