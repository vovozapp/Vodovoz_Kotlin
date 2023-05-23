package com.vodovoz.app.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.like.LikeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountManager: AccountManager,
    private val likeManager: LikeManager
) : ViewModel() {

    init {
        viewModelScope.launch {
            likeManager.updateStateFromLikesLocal()
        }
    }

    fun sendFirebaseToken() {
        viewModelScope.launch {
            kotlin.runCatching {
                accountManager.sendFirebaseToken()
            }
        }
    }

}