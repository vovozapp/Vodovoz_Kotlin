package com.vodovoz.app.feature.product_comments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.bottomitem.BottomProgressItem
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.design_system.model.CommentUi
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.product_comments.model.ProductCommentsInfoUi
import com.vodovoz.app.feature.product_comments.model.SortUi
import com.vodovoz.app.feature.product_comments.model.toDomain
import com.vodovoz.app.feature.product_comments.model.toUi
import com.vodovoz.app.mapper.CommentMapper.mapToUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductCommentsFlowViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val accountManager: AccountManager,
    private val vodovozServiceRepository: VodovozServiceRepository,
) : PagingContractViewModel<ProductCommentsFlowViewModel.ProductCommentsState, ProductCommentsFlowViewModel.ProductCommentsEvents>(
    ProductCommentsState()
) {

    private val productId = savedState.get<Long>("productId")

    private fun listenUserLoginStatus() = viewModelScope.launch {
        accountManager.observeAccountId().collectLatest { id ->
            if (id == null) uiStateListener.updateData { s -> s.copy(showWriteComment = false) }
            else uiStateListener.updateData { s -> s.copy(showWriteComment = true) }
        }
    }

    private fun fetchProductComments() = viewModelScope.launch {
        if (productId == null) return@launch

        val productCommentsInfoResult =
            vodovozServiceRepository.getProductCommentsInfo(productId).firstOrNull()
        val productCommentsInfo = productCommentsInfoResult?.getOrNull()

        if (productCommentsInfo != null) {
            uiStateListener.updateData { s ->
                val uiInfo = productCommentsInfo.toUi()
                val currentSort = uiInfo.sorting.firstOrNull() ?: SortUi.Empty
                s.copy(
                    productCommentsInfo = uiInfo,
                    currentSort = uiInfo.sorting.firstOrNull() ?: SortUi.Empty,
                    pagedComments = vodovozServiceRepository.getProductCommentsPaged(
                        productId, currentSort.toDomain()
                    ).map { pagingData ->
                        pagingData.map { comment ->
                            comment.toUi()
                        }
                    }
                )
            }

            listenUserLoginStatus()
        }

        productCommentsInfoResult?.onFailure {
            //todo - handle fail
        } ?: run {
            //todo - handle flow fails
        }
    }

    fun firstLoadSorted() {
        if (!state.isFirstLoad) {
            uiStateListener.value =
                state.copy(isFirstLoad = true, loadingPage = true)
            fetchProductComments()
        }
    }

    fun refreshSorted() {
        uiStateListener.value =
            state.copy(loadingPage = true, page = 1, loadMore = false, bottomItem = null)
        fetchProductComments()
    }

    fun loadMoreSorted() {
        if (state.bottomItem == null && state.page != null) {
            uiStateListener.value = state.copy(loadMore = true, bottomItem = BottomProgressItem())
            fetchProductComments()
        }
    }

    fun onSendCommentClick() {
        viewModelScope.launch {
            val id = accountManager.fetchAccountId()
            if (id == null) {
                eventListener.emit(ProductCommentsEvents.GoToProfile)
            } else {
                eventListener.emit(ProductCommentsEvents.SendComment)
            }
        }
    }

    fun isLoginAlready() = accountManager.isAlreadyLogin()

    fun selectSort(sort: SortUi) = viewModelScope.launch {
        eventListener.emit(ProductCommentsEvents.ScrollToTop)
        uiStateListener.updateData { d ->
            d.copy(
                currentSort = sort,
                pagedComments = vodovozServiceRepository.getProductCommentsPaged(
                    productId ?: return@updateData d.copy(currentSort = sort), sort.toDomain()
                ).map { pagingData ->
                    pagingData.map { comment ->
                        comment.toUi()
                    }
                }
            )
        }
    }

    fun navigateToWriteComment() = viewModelScope.launch {

    }

    fun navigateBack() = viewModelScope.launch {
        eventListener.emit(ProductCommentsEvents.GoBack)
    }

    sealed class ProductCommentsEvents : Event {
        data object SendComment : ProductCommentsEvents()
        data object GoToProfile : ProductCommentsEvents()
        data object ScrollToTop : ProductCommentsEvents()
        data object GoBack: ProductCommentsEvents()
    }

    data class ProductCommentsState(
        val itemsList: List<Item> = emptyList(),
        val productCommentsInfo: ProductCommentsInfoUi = ProductCommentsInfoUi.Empty,
        val pagedComments: Flow<PagingData<CommentUi>> = emptyFlow(),
        val currentSort: SortUi = SortUi.Empty,
        val showWriteComment: Boolean = false,
    ) : State {
    }
}