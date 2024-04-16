package com.vodovoz.app.data.parser.response.site_state

import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.feature.sitestate.model.Generation
import com.vodovoz.app.feature.sitestate.model.SiteStateContact
import com.vodovoz.app.feature.sitestate.model.SiteStateData
import com.vodovoz.app.feature.sitestate.model.SiteStateResponse
import okhttp3.ResponseBody
import org.json.JSONObject

object SiteStateResponseParser {
    fun ResponseBody.parseResponseSiteState(): SiteStateResponse {
        val responseJson = JSONObject(string())
        return SiteStateResponse(
            active = responseJson.safeString("ACTIVE"),
            data = if (responseJson.has("DATA") && !responseJson.isNull("DATA")) {
                responseJson.getJSONObject("DATA").parseJsonSiteData()
            } else {
                null
            },
            showComments = responseJson.getBoolean("COMMENTFILES"),
            requestUrl = responseJson.safeString("SMSRASSILKA"),
            generation = if (responseJson.has("GENERATION") && !responseJson.isNull("GENERATION")) {
                responseJson.getJSONObject("GENERATION").parrseJsonGeneration()
            } else {
                null
            },
        )
    }

    private fun JSONObject.parrseJsonGeneration() = Generation(
        enabled = getBoolean("TRAKING"),
        time = safeString("TIME"),
    )

    private fun JSONObject.parseJsonSiteData() = SiteStateData(
        title = safeString("TITLE"),
        logo = safeString("LOGO"),
        desc = safeString("OPISANIE"),
        email = safeString("EMAIL"),
        whatsUp = if (has("WATSAP")) {
            getJSONObject("WATSAP").parseJsonContact()
        } else {
            null
        },
        viber = if (has("VIBER")) {
            getJSONObject("VIBER").parseJsonContact()
        } else {
            null
        },
        telegram = if (has("TELEGA")) {
            getJSONObject("TELEGA").parseJsonContact()
        } else {
            null
        },
        chat = if (has("JIVOSAIT")) {
            getJSONObject("JIVOSAIT").parseJsonContact()
        } else {
            null
        },
        phone = if (has("PHONE")) {
            getJSONObject("PHONE").parseJsonContact()
        } else {
            null
        },
        time = safeString("TIME")
    )

    private fun JSONObject.parseJsonContact() = SiteStateContact(
        url = safeString("URL"),
        image = safeString("IMAGES"),
    )


}