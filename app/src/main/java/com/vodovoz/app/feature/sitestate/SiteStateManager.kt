package com.vodovoz.app.feature.sitestate

import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.feature.sitestate.model.SiteStateResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SiteStateManager @Inject constructor(
    private val repository: MainRepository
) {

    private var siteStateListener = MutableStateFlow<SiteStateResponse?>(null)
    fun observeSiteState() = siteStateListener.asStateFlow()

    private val deepLinkPathListener = MutableStateFlow<String?>(null)
    fun observeDeepLinkPath() = deepLinkPathListener.asStateFlow()

    suspend fun requestSiteState() {
        if (siteStateListener.value == null) {
            kotlin.runCatching {
                siteStateListener.value = repository.fetchSiteState()
            }.onFailure {
                siteStateListener.value = null
            }
        }
    }

    suspend fun fetchSiteStateActive() : Boolean {
        val stateActive = siteStateListener.value?.active ?: kotlin.run {
            requestSiteState()
            return false
        }
        return when(stateActive) {
            "N" -> true
            else -> false
        }
    }

    fun saveDeepLinkPath(path: String?) {
        if (path != null) {
            deepLinkPathListener.value = path
        }
    }

    fun clearDeepLinkListener() {
        deepLinkPathListener.value = null
    }
}