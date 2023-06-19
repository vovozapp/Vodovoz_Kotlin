package com.vodovoz.app.feature.main

import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.local.LocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val localDataSource: LocalDataSource,
) : ViewModel() {

    var isBottomBarInited = false

    override fun onCleared() {
        localDataSource.removeCookieSessionId()
        super.onCleared()
    }
}