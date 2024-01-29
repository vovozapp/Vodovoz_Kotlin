package com.vodovoz.app.data.parser.response.certificate

import com.vodovoz.app.data.model.common.CertificatePropertyEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.features.ActivateCertificateBundleEntity
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object ActivateCertificateInfoJsonParser {
    fun ResponseBody.parseActivateCertificateInfoResponse(): ResponseEntity<ActivateCertificateBundleEntity> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> {
                val data = responseJson.getJSONArray("data").parseCertificatePropertyEntityList()
                ResponseEntity.Success(
                    ActivateCertificateBundleEntity(
                        title = responseJson.getString("title"),
                        details = if (data.isNotEmpty()) {
                            data[0].text
                        } else {
                            ""
                        },
                        certificatePropertyEntityList = data
                    )
                )
            }

            else -> ResponseEntity.Error("Неправильный запрос")
        }
    }

    private fun JSONArray.parseCertificatePropertyEntityList(): List<CertificatePropertyEntity> =
        mutableListOf<CertificatePropertyEntity>().also { list ->
            for (index in 0 until length()) {
                list.add(getJSONObject(index).parseCertificatePropertyEntity())
            }
        }

    private fun JSONObject.parseCertificatePropertyEntity() = CertificatePropertyEntity(
        title = safeString("TITLE"),
        textToField = safeString("TEXT_V_POLE"),
        text = safeString("TEXT"),
        buttonText = safeString("KNOPKA"),
        buttonColor = if (has("BACKGROUND") && !isNull("BACKGROUND")) {
            getJSONObject("BACKGROUND").safeString("ANDROID")
        } else {
            ""
        }
    )


}