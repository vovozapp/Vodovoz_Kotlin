package com.vodovoz.app.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.token.FirebaseTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val likeManager: LikeManager,
    private val firebaseTokenManager: FirebaseTokenManager,
) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            likeManager.syncFavoritesFromLocal()
        }
    }

    fun sendFirebaseToken() {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                firebaseTokenManager.sendFirebaseToken()
            }
        }
    }

}