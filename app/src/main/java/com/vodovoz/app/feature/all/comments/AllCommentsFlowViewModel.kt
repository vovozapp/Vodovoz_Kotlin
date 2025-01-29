package com.vodovoz.app.feature.all.comments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.bottomitem.BottomProgressItem
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.CommentMapper.mapToUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllCommentsFlowViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val accountManager: AccountManager,
) : PagingContractViewModel<AllCommentsFlowViewModel.AllCommentsState, AllCommentsFlowViewModel.AllCommentsEvents>(
    AllCommentsState()
) {

    private val productId = savedState.get<Long>("productId")

    private fun fetchAllCommentsByProductId() {
        if (productId == null) return
        viewModelScope.launch {
            flow {
                emit(
                    repository.fetchAllCommentsByProduct(
                        productId = productId,
                        page = state.page
                    )
                )
            }
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        uiStateListener.value = if (data.comments.isEmpty() && !state.loadMore) {
                            state.copy(
                                error = ErrorState.Empty(),
                                loadingPage = false,
                                loadMore = false,
                                bottomItem = null,
                                page = 1
                            )
                        } else {

                            val itemsList = if (state.loadMore) {
                                state.data.itemsList + data.comments
                            } else {
                                mutableListOf<Item>().apply {
                                    add(data.commentsData)
                                    addAll(data.comments)
                                }
                            }

                            state.copy(
                                page = if (data.comments.isEmpty()) null else state.page?.plus(1),
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
                .catch {
                    debugLog { "fetch all comments error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
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

    fun onSendCommentClick() {
        viewModelScope.launch {
            val id = accountManager.fetchAccountId()
            if (id == null) {
                eventListener.emit(AllCommentsEvents.GoToProfile)
            } else {
                eventListener.emit(AllCommentsEvents.SendComment)
            }
        }
    }

    fun isLoginAlready() = accountManager.isAlreadyLogin()

    sealed class AllCommentsEvents : Event {
        object SendComment : AllCommentsEvents()
        object GoToProfile : AllCommentsEvents()
    }

    data class AllCommentsState(
        val itemsList: List<Item> = emptyList(),
    ) : State
}