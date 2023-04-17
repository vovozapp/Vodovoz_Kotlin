package com.vodovoz.app.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountManager: AccountManager
) : ViewModel() {

    fun sendFirebaseToken() {
        viewModelScope.launch {
            accountManager.sendFirebaseToken()
        }
    }

}