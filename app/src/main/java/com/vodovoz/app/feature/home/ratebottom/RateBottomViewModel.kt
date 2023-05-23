package com.vodovoz.app.feature.home.ratebottom

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.feature.home.ratebottom.model.CollapsedData
import com.vodovoz.app.feature.home.ratebottom.model.CollapsedDataImage
import com.vodovoz.app.feature.home.ratebottom.model.RateBottomModel
import com.vodovoz.app.feature.sitestate.SiteStateManager
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RateBottomViewModel @Inject constructor(
    private val repository: MainRepository,
    private val accountManager: AccountManager,
    private val siteStateManager: SiteStateManager
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
                .flowOn(Dispatchers.IO)
                .onEach {
                    uiStateListener.value = if (it.status == "Success") {
                        siteStateManager.showRateBottom = false
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(
                                item = it,
                                collapsedData = it.mapToCollapsedData()
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
                .catch {
                    debugLog { "fetch rate data error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(
                            error = it.toErrorState(),
                            loadingPage = false
                        )
                }
                .collect()
        }
    }

    data class RateBottomState(
        val item: RateBottomModel? = null,
        val collapsedData: CollapsedData? = null,
        val expandedData: RateBottomModel? = null
    ) : State

    fun RateBottomModel.mapToCollapsedData() : CollapsedData {
        return CollapsedData(
            this.rateBottomData?.titleCategory,
            this.rateBottomData?.allProductsCount,
            this.rateBottomData?.productsList?.map {
                CollapsedDataImage(it.image)
            }
        )
    }
}