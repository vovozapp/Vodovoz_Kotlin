package com.vodovoz.app.feature.all.comments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.bottomitem.BottomProgressItem
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.comment.AllCommentsByProductResponseJsonParser.parseAllCommentsByProductResponse
import com.vodovoz.app.mapper.CommentMapper.mapToUI
import com.vodovoz.app.mapper.ProductMapper.mapToUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllCommentsFlowViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val dataRepository: DataRepository
): PagingStateViewModel<AllCommentsFlowViewModel.AllCommentsState>(AllCommentsState()) {

    private val productId = savedState.get<Long>("productId")

    private fun fetchAllCommentsByProductId() {
        if (productId == null) return
        viewModelScope.launch {
            flow { emit(repository.fetchAllCommentsByProduct(productId = productId, page = state.page)) }
                .catch {
                    debugLog { "fetch all comments sorted error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseAllCommentsByProductResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        uiStateListener.value = if (data.isEmpty() && !state.loadMore) {
                            state.copy(
                                error = ErrorState.Empty(),
                                loadingPage = false,
                                loadMore = false,
                                bottomItem = null,
                                page = 1
                            )
                        } else {

                            val itemsList = if (state.loadMore) {
                                state.data.itemsList + data
                            } else {
                                data
                            }

                            state.copy(
                                page = if (data.isEmpty()) null else state.page?.plus(1),
                                loadingPage = false,
                                data = state.data.copy(itemsList = itemsList),
                                error = null,
                                loadMore = false,
                                bottomItem = null
                            )
                        }
                    } else {
                        uiStateListener.value =
                            state.copy(
                                loadingPage = false,
                                error = ErrorState.Error(),
                                page = 1,
                                loadMore = false
                            )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun firstLoadSorted() {
        if (!state.isFirstLoad) {
            uiStateListener.value =
                state.copy(isFirstLoad = true, loadingPage = true)
            fetchAllCommentsByProductId()
        }
    }

    fun refreshSorted() {
        uiStateListener.value =
            state.copy(loadingPage = true, page = 1, loadMore = false, bottomItem = null)
        fetchAllCommentsByProductId()
    }

    fun loadMoreSorted() {
        if (state.bottomItem == null && state.page != null) {
            uiStateListener.value = state.copy(loadMore = true, bottomItem = BottomProgressItem())
            fetchAllCommentsByProductId()
        }
    }

    fun isLoginAlready() = dataRepository.isAlreadyLogin()

    data class AllCommentsState(
        val itemsList: List<Item> = emptyList()
    ) : State
}