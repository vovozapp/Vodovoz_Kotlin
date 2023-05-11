package com.vodovoz.app.feature.home.ratebottom

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.feature.home.ratebottom.model.RateBottomModel
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RateBottomViewModel @Inject constructor(
    private val repository: MainRepository,
    private val accountManager: AccountManager
) : PagingStateViewModel<RateBottomViewModel.RateBottomState>(RateBottomState()) {

    fun firstLoad() {
        if (!state.isFirstLoad) {
            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            fetchRateBottomData()
        }
    }

    fun refresh() {
        uiStateListener.value = state.copy(loadingPage = true)
        fetchRateBottomData()
    }

    private fun fetchRateBottomData() {
        val userId = accountManager.fetchAccountId() ?: return

        viewModelScope.launch {
            flow { emit(repository.fetchRateBottomData(userId)) }
                .catch {
                    debugLog { "fetch rate data error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(
                            error = it.toErrorState(),
                            loadingPage = false
                        )
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    if (it.status == "Success") {
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(
                                item = it
                            ),
                            error = null
                        )
                    } else {
                        state.copy(
                            loadingPage = false,
                            error = ErrorState.Error()
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    data class RateBottomState(
        val item: RateBottomModel? = null
    ) : State
}