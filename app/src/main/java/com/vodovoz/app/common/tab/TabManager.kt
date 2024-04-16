package com.vodovoz.app.common.tab

import com.vodovoz.app.R
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.BottomCartMapper.mapToUI
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TabManager @Inject constructor(
    private val repository: MainRepository,
) {

    private val tabStateListener = MutableSharedFlow<Int>()
    fun observeTabState() = tabStateListener.asSharedFlow()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val bottomNavCartStateListener = MutableStateFlow<BottomNavCartState?>(null)
    fun observeBottomNavCartState() = bottomNavCartStateListener.asStateFlow()

    private val bottomNavProfileStateListener = MutableStateFlow<Int?>(null)
    fun observeBottomNavProfileState() = bottomNavProfileStateListener.asStateFlow()

//    private val loadingAddToCartListener = MutableSharedFlow<BottomNavCartState>()
//    fun observeAddToCartLoading() = loadingAddToCartListener.asSharedFlow()

    private val tabReselectListener = MutableStateFlow(DEFAULT_STATE)
    fun observeTabReselect() = tabReselectListener.asStateFlow()
    fun setDefaultState() {
        tabReselectListener.value = DEFAULT_STATE
    }

    private val tabAuthRedirectListener = MutableStateFlow<Int>(DEFAULT_AUTH_REDIRECT)
    fun fetchAuthRedirect() = tabAuthRedirectListener.value

    private val addressesRefreshListener = MutableStateFlow(ADDRESSES_DEFAULT_STATE)
    fun observeAddressesRefresh() = addressesRefreshListener.asStateFlow()

    private val tabVisibilityListener = MutableStateFlow(true)
    fun observeTabVisibility() = tabVisibilityListener.asStateFlow()

    fun setAddressesRefreshState(refresh: Boolean) {
        addressesRefreshListener.value = refresh
    }

    fun setAuthRedirect(graphId: Int) {
        tabAuthRedirectListener.value = graphId
    }

    fun setDefaultAuthRedirect() {
        tabAuthRedirectListener.value = DEFAULT_AUTH_REDIRECT
    }

    fun selectTab(id: Int) {
        scope.launch {
            tabStateListener.emit(id)
        }
    }

    fun reselect(id: Int) {
        tabReselectListener.value = id
    }

    fun saveBottomNavCartState() {
        scope.launch {
            flow {
                emit(repository.fetchBottomCart())
            }.onEach { response ->
                if (response is ResponseEntity.Success) {
                    val bottomCartUI = response.data.mapToUI()
                    bottomNavCartStateListener.value = BottomNavCartState(
                        count = bottomCartUI.productCount,
                        total = bottomCartUI.totalSum.toInt()
                    )
                } else if (response is ResponseEntity.Error) {
                    debugLog { "Error while fetching bottom cart state: ${response.errorMessage}" }
                }
            }.catch {
                debugLog { "Error while fetching bottom cart state: $it" }
            }.collect()
        }
    }

    fun saveBottomNavProfileState(amount: Int?) {
        bottomNavProfileStateListener.value = amount
    }

    fun changeTabVisibility(vis: Boolean) {
        tabVisibilityListener.value = vis
    }

    fun clearBottomNavCartState() {
        bottomNavCartStateListener.value = null
    }

    fun clearBottomNavProfileState() {
        bottomNavProfileStateListener.value = null
    }

    data class BottomNavCartState(
        val count: Int,
        val total: Int,
    )

    companion object {
        const val DEFAULT_STATE = -1

        @JvmField
        val DEFAULT_AUTH_REDIRECT = R.id.graph_profile
        const val ADDRESSES_DEFAULT_STATE = false
    }
}