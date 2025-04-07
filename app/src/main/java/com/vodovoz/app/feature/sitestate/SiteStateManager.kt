package com.vodovoz.app.feature.sitestate

import com.vodovoz.app.common.agreement.AgreementController
import com.vodovoz.app.common.jivochat.JivoChatController
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.parser.common.safeString
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.sitestate.model.SiteStateResponse
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.single
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SiteStateManager @Inject constructor(
    private val repository: MainRepository,
    private val vodovozServiceRepository: VodovozServiceRepository,
) {

    var showRateBottom: Boolean? = null

    private var siteStateListener = MutableStateFlow<SiteStateResponse?>(null)
    fun observeSiteState() = siteStateListener.asStateFlow()

    val siteStateSnapshot get() = siteStateListener.value

    private val deepLinkPathListener = MutableStateFlow<String?>(null)
    fun observeDeepLinkPath() = deepLinkPathListener.asStateFlow()

    private val pushListener = MutableStateFlow<PushData?>(null)
    fun observePush() = pushListener.asStateFlow()

    suspend fun requestSiteState(): SiteStateResponse? {
        if (siteStateListener.value == null) {
            runCatching {
                //New api
//                val siteState = vodovozServiceRepository.getSiteState().single().getOrThrow()
//                val siteAgreement = siteState.agreement
//                val jivoChat = siteState.jivoChat

                //TODO - change to new api if all correctly
                val siteState = repository.fetchSiteState()
                siteStateListener.value = siteState
                val siteAgreement = siteState.agreement
                val jivoChat = siteState.jivoChat

                AgreementController.setAgreement(
                    text = siteAgreement?.text,
                    titles = siteAgreement?.titles,
                )
                JivoChatController.setParams(
                    active = jivoChat?.active ?: false,
                    link = jivoChat?.url ?: "",
                )
            }.onFailure {
                siteStateListener.value = null
            }
        }
        return siteStateListener.value
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