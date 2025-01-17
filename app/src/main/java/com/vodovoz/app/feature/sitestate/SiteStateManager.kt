package com.vodovoz.app.feature.sitestate

import com.vodovoz.app.common.agreement.AgreementController
import com.vodovoz.app.common.jivochat.JivoChatController
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.feature.sitestate.model.SiteStateResponse
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SiteStateManager @Inject constructor(
    private val repository: MainRepository,
) {

    var showRateBottom: Boolean? = null

    private var siteStateListener = MutableStateFlow<SiteStateResponse?>(null)
    fun observeSiteState() = siteStateListener.asStateFlow()

    private val deepLinkPathListener = MutableStateFlow<String?>(null)
    fun observeDeepLinkPath() = deepLinkPathListener.asStateFlow()

    private val pushListener = MutableStateFlow<PushData?>(null)
    fun observePush() = pushListener.asStateFlow()

    suspend fun requestSiteState() {
        if (siteStateListener.value == null) {
            runCatching {
                siteStateListener.value = repository.fetchSiteState()
                AgreementController.setAgreement(
                    text = siteStateListener.value?.agreement?.text,
                    titles = siteStateListener.value?.agreement?.titles,
                )
                JivoChatController.setParams(
                    siteStateListener.value?.jivoChat?.active ?: false,
                    siteStateListener.value?.jivoChat?.url ?: "",
                )
            }.onFailure {
                siteStateListener.value = null
            }
        }
    }

    suspend fun fetchSiteStateActive(): Boolean {
        return when (siteStateListener.value?.active) {
            "N" -> true
            else -> {
                requestSiteState()
                false
            }
        }
    }

    fun saveDeepLinkPath(path: String?) {
        if (path != null) {
            deepLinkPathListener.value = path
        }
    }

    fun savePushData(json: JSONObject) {
        debugLog { "save push data $json $this" }
        val path = json.safeString("Secreen")
        val id = json.safeString("ID")
        val subsections = json.safeString("SUBSECTIONS")
        val orderId = json.safeString("NumberZakaz")
        val section = json.safeString("NAME_RAZDEL")
        val action = json.safeString("ACTION")

        if (path.isNotEmpty()) {
            pushListener.value = PushData(id, section, orderId, subsections, action, path)
        }
    }

    fun clearDeepLinkListener() {
        deepLinkPathListener.value = null
    }

    fun clearPushListener() {
        debugLog { "clear push data" }
        pushListener.value = null
    }

    data class PushData(
        val id: String? = null,
        val section: String? = null,
        val orderId: String? = null,
        val subsections: String? = null,
        val action: String? = null,
        val path: String,
    )
}