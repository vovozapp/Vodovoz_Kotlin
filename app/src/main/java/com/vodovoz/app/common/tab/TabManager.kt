package com.vodovoz.app.common.tab

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

    private val loadingAddToCartListener = MutableSharedFlow<BottomNavCartState>()
    fun observeAddToCartLoading() = loadingAddToCartListener.asSharedFlow()

    fun selectTab(id: Int) {
        scope.launch {
            tabStateListener.emit(id)
        }
    }

    fun saveBottomNavCartState(count: Int, total: Int) {
        bottomNavCartStateListener.value = BottomNavCartState(count, total)
    }

    fun loadingAddToCart(load: Boolean) {
        scope.launch {
            loadingAddToCartListener.emit(
                if (load) {
                    BottomNavCartState(bottomNavCartStateListener.value?.count?.plus(1) ?:0, -1)
                } else {
                    BottomNavCartState(bottomNavCartStateListener.value?.count ?:0, -2)
                }
            )
        }
    }

    fun clearBottomNavCartState() {
        bottomNavCartStateListener.value = null
    }

    data class BottomNavCartState(
        val count: Int,
        val total: Int
    )
}