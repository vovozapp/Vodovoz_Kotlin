package com.vodovoz.app.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.token.FirebaseTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val likeManager: LikeManager,
    private val firebaseTokenManager: FirebaseTokenManager,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

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

    fun finishLoading() = viewModelScope.launch {
        _isLoading.update { false }
    }

}