package com.vodovoz.app.common.tab

import com.vodovoz.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TabManager @Inject constructor() {

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
    fun setDefaultState() { tabReselectListener.value = DEFAULT_STATE }

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

    fun saveBottomNavCartState(count: Int, total: Int) {
        bottomNavCartStateListener.value = BottomNavCartState(count, total)
    }

    fun saveBottomNavProfileState(amount: Int?) {
        bottomNavProfileStateListener.value = amount
    }

    fun changeTabVisibility(vis: Boolean) {
        tabVisibilityListener.value = vis
    }

//    fun loadingAddToCart(load: Boolean, plus: Boolean) {
//        scope.launch {
//            bottomNavCartStateListener.emit(
//                if (load) {
//                    if (plus) {
//                        BottomNavCartState(
//                            bottomNavCartStateListener.value?.count?.plus(1) ?: 0,
//                            -1
//                        )
//                    } else {
//                        BottomNavCartState(
//                            bottomNavCartStateListener.value?.count?.minus(1) ?: 0,
//                            -1
//                        )
//                    }
//                } else {
//                    BottomNavCartState(bottomNavCartStateListener.value?.count ?:0, -2)
//                }
//            )
//        }
//    }

    fun clearBottomNavCartState() {
        bottomNavCartStateListener.value = null
    }

    fun clearBottomNavProfileState() {
        bottomNavProfileStateListener.value = null
    }

    data class BottomNavCartState(
        val count: Int,
        val total: Int
    )

    companion object {
        const val DEFAULT_STATE = -1
        @JvmField
        val DEFAULT_AUTH_REDIRECT = R.id.graph_profile
        const val ADDRESSES_DEFAULT_STATE = false
    }
}