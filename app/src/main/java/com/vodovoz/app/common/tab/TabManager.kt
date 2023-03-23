package com.vodovoz.app.common.tab

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TabManager @Inject constructor() {

    private val tabStateListener = MutableSharedFlow<Int>()
    fun observeTabState() = tabStateListener.asSharedFlow()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun selectTab(id: Int) {
        scope.launch {
            tabStateListener.emit(id)
        }
    }

    fun init() {

    }
}