package com.vodovoz.app.data.parser.response.map

import com.vodovoz.app.data.model.common.AddressEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object AddressByGeocodeResponseJsonParser {

    fun ResponseBody.parseAddressByGeocodeResponse(): ResponseEntity<AddressEntity> {
        val responseJson = JSONObject(string())
        return ResponseEntity.Success(responseJson.getJSONObject("response").parseAddressEntity())
    }

    private fun JSONObject.parseAddressEntity(): AddressEntity {
        if (has("GeoObjectCollection")) {
            if (getJSONObject("GeoObjectCollection").has("featureMember")) {
                if (getJSONObject("GeoObjectCollection").getJSONArray("featureMember").length() != 0) {
                    if (getJSONObject("GeoObjectCollection")
                            .getJSONArray("featureMember")
                            .getJSONObject(0)
                            .has("GeoObject")
                    ) {
                        if (getJSONObject("GeoObjectCollection")
                                .getJSONArray("featureMember")
                                .getJSONObject(0)
                                .getJSONObject("GeoObject")
                                .getJSONObject("metaDataProperty")
                                .has("GeocoderMetaData")
                        ) {
                            if (getJSONObject("GeoObjectCollection")
                                    .getJSONArray("featureMember")
                                    .getJSONObject(0)
                                    .getJSONObject("GeoObject")
                                    .getJSONObject("metaDataProperty")
                                    .getJSONObject("GeocoderMetaData")
                                    .has("Address")
                            ) {
                                if (getJSONObject("GeoObjectCollection")
                                        .getJSONArray("featureMember")
                                        .getJSONObject(0)
                                        .getJSONObject("GeoObject")
                                        .getJSONObject("metaDataProperty")
                                        .getJSONObject("GeocoderMetaData")
                                        .getJSONObject("Address")
                                        .has("Components")
                                ) {
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

                                    return AddressEntity(
                                        fullAddress = fullAddress,
                                        locality = addressComponentList.find { it.first == "locality" }?.second ?: "1",
                                        street = addressComponentList.find { it.first == "street" }?.second ?: "2",
                                        house = addressComponentList.find { it.first == "house" }?.second ?: "3"
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