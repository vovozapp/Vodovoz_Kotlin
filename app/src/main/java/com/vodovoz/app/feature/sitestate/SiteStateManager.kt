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

    suspend fun requestSiteState() {
        if (siteStateListener.value == null) {
            kotlin.runCatching {
                siteStateListener.value = repository.fetchSiteState()
            }.onFailure {
                siteStateListener.value = null
            }
        }
    }
}