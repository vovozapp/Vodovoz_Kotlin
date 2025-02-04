package com.vodovoz.app.feature.promotiondetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.promotion.PromotionDetailResponseJsonParser
import com.vodovoz.app.design_system.model.PromotionDetailsUi
import com.vodovoz.app.design_system.model.mapToUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.home.model.ProductUi
import com.vodovoz.app.feature.home.model.mapToUi
import com.vodovoz.app.mapper.PromotionDetailMapper.mapToUI
import com.vodovoz.app.ui.model.PromotionDetailUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromotionDetailFlowViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val accountManager: AccountManager,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager,
    private val vodovozServiceRepository: VodovozServiceRepository,
) : PagingContractViewModel<PromotionDetailFlowViewModel.PromotionDetailFlowState, PromotionDetailFlowViewModel.PromotionDetailEvent>(
    PromotionDetailFlowState()
) {

    private var promotionId = savedState.get<Long>("promotionId")?.toInt() ?: kotlin.run {
        navigateBack()
        -1
    }

    private fun loadData() {
        vodovozServiceRepository.getPromotionDetails(promotionId)
            .onEach { promotionDetailsResult ->
                promotionDetailsResult.onSuccess { titleAndPromotionDetails ->

                    val products =
                        vodovozServiceRepository.getPromotionDetailsProductsPaged(promotionId)
                            .map { pagingData -> pagingData.map { productModel -> productModel.mapToUi() } }

                    uiStateListener.updateData { s ->
                        s.copy(
                            promotionDetails = titleAndPromotionDetails.second.mapToUi(),
                            products = products,
                            productsTitle = titleAndPromotionDetails.first.title
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun navigateBack() = viewModelScope.launch {
        eventListener.emit(PromotionDetailEvent.GoBack)
    }

    private fun fetchPromotionDetails() {

        viewModelScope.launch {
            val promoId = promotionId ?: return@launch
            flow { emit(repository.fetchPromotionDetails(promoId.toLong())) }
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        val data = response.data.detail?.mapToUI()
                        val dataError = response.data.detailError?.mapToUI()

                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                items = data,
                                errorItem = dataError
                            ),
                            loadingPage = false,
                            error = null
                        )

                    } else {
                        uiStateListener.value =
                            state.copy(
                                loadingPage = false,
                                error = ErrorState.Error()
                            )
                    }
                }
                .flowOn(Dispatchers.Default)
                .catch {
                    debugLog { "fetch promotion details sorted error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun firstLoadSorted() {
        loadData()
        if (!state.isFirstLoad) {
            uiStateListener.value =
                state.copy(isFirstLoad = true, loadingPage = true)
            //todo - uncomment or delete
            //fetchPromotionDetails()
        }
    }

    fun refreshSorted() {
        uiStateListener.value =
            state.copy(loadingPage = true, page = 1, loadMore = false, bottomItem = null)
        fetchPromotionDetails()
    }

    fun isLoginAlready() = accountManager.isAlreadyLogin()

    fun changeCart(productId: Long, quantity: Int, oldQuan: Int) {
        viewModelScope.launch {
            cartManager.add(id = productId, oldCount = oldQuan, newCount = quantity)
        }
    }

    fun changeRating(productId: Long, rating: Float, oldRating: Float) {
        viewModelScope.launch {
            ratingProductManager.rate(productId, rating = rating, oldRating = oldRating)
        }
    }

    fun changeFavoriteStatus(productId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            likeManager.like(productId, !isFavorite)
        }
    }

    data class PromotionDetailFlowState(
        val items: PromotionDetailUI? = null,
        val errorItem: PromotionDetailResponseJsonParser.PromotionDetailErrorUI? = null,
        val promotionDetails: PromotionDetailsUi = PromotionDetailsUi.Empty,
        val productsTitle: String = "",
        val products: Flow<PagingData<ProductUi>> = emptyFlow(),
    ) : State

    sealed class PromotionDetailEvent : Event {

        data object GoBack : PromotionDetailEvent()

    }
}