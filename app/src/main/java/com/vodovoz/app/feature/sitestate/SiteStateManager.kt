package com.vodovoz.app.feature.sitestate

import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.parser.common.safeInt
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
    private val repository: MainRepository
) {

    var showRateBottom: Boolean?= null

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
            }.onFailure {
                siteStateListener.value = null
            }
        }
    }

    suspend fun fetchSiteStateActive() : Boolean {
        return when(siteStateListener.value?.active) {
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
        debugLog { "save push data $json" }
        val path = json.safeString("Secreen")
        val id = json.safeString("ID")
        val subsections = json.safeString("SUBSECTIONS")
        val orderId = json.safeString("NumberZakaz")
        val section = json.safeString("NAME_RAZDEL")
        if (path.isNotEmpty()) {
            pushListener.value = PushData(id, section, orderId, subsections, path)
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
        val path: String
    )
}