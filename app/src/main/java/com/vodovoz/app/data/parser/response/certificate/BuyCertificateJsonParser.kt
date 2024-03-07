package com.vodovoz.app.data.parser.response.certificate

import com.vodovoz.app.data.model.common.BuyCertificateField
import com.vodovoz.app.data.model.common.BuyCertificatePayment
import com.vodovoz.app.data.model.common.BuyCertificatePropertyEntity
import com.vodovoz.app.data.model.common.PayMethodEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.features.BuyCertificateBundleEntity
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.data.remote.ResponseStatus
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

object BuyCertificateJsonParser {

    fun ResponseBody.parseBuyCertificateResponse(): ResponseEntity<BuyCertificateBundleEntity> {
        val responseJson = JSONObject(string())
        return when (responseJson.getString("status")) {
            ResponseStatus.SUCCESS -> ResponseEntity.Success(
                BuyCertificateBundleEntity(
                    title = responseJson.getString("title"),
                    payment = responseJson.getJSONObject("oplata").parseBuyCertificatePayment(),
                    buyCertificatePropertyEntityList = responseJson.getJSONArray("data")
                        .parseBuyCertificatePropertyEntityList()
                )
            )

            else -> ResponseEntity.Error(responseJson.getString("message"))
        }
    }

    private fun JSONObject.parseBuyCertificatePayment() = BuyCertificatePayment(
        code = safeString("CODE"),
        name = safeString("NAME"),
        required = safeString("OBAZATELEN"),
        payMethods = getJSONArray("DATA").parsePayMethodEntityList()
    )

    private fun JSONArray.parsePayMethodEntityList(): List<PayMethodEntity> =
        mutableListOf<PayMethodEntity>().also { list ->
            for (index in 0 until length()) list.add(getJSONObject(index).parsePayMethodEntity())
        }

    private fun JSONObject.parsePayMethodEntity() = PayMethodEntity(
        id = getString("VALUE").toLong(),
        name = getString("TEXT")
    )

    private fun JSONArray.parseBuyCertificatePropertyEntityList(): List<BuyCertificatePropertyEntity> =
        mutableListOf<BuyCertificatePropertyEntity>().also { list ->
            for (index in 0 until length()) list.add(getJSONObject(index).parseBuyCertificateProperty())
        }

    private fun JSONObject.parseBuyCertificateProperty() = BuyCertificatePropertyEntity(
        name = safeString("NAME"),
        field = safeString("POLE"),
        text = safeString("TEXT"),
        required = safeString("OBAZATELEN"),
        code = safeString("CODE"),
        value = safeString("VALUE"),
        buyCertificateFields = if (has("VID")) {
            getJSONArray("VID").parseBuyCertificateFields()
        } else {
            null
        },
        showAmount = if (has("QUANITY")) {
            safeString("QUANITY").equals("Y")
        } else {
            false
        }
    )

    private fun JSONArray.parseBuyCertificateFields() =
        mutableListOf<BuyCertificateField>().also { list ->
            for (index in 0 until length()) list.add(getJSONObject(index).parseBuyCertificateField())
        }

    private fun JSONObject.parseBuyCertificateField() = BuyCertificateField(
        id = safeString("ID"),
        name = safeString("NAME"),
    )


}