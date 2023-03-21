package com.vodovoz.app.feature.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.LocalSyncExtensions.syncCartQuantity
import com.vodovoz.app.data.LocalSyncExtensions.syncFavoriteProducts
import com.vodovoz.app.data.LocalSyncExtensions.syncFavoriteStatus
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.product.ProductDetailsResponseJsonParser.parseProductDetailsResponse
import com.vodovoz.app.feature.productdetail.viewholders.detailheader.DetailHeader
import com.vodovoz.app.feature.productdetail.viewholders.detailprices.DetailPrices
import com.vodovoz.app.mapper.ProductDetailBundleMapper.mapToUI
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.model.*
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsFlowViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val mainRepository: MainRepository,
    private val localDataSource: LocalDataSource,
    private val cartManager: CartManager,
    private val likeManager: LikeManager
) : ViewModel() {

    private val uiStateListener = MutableStateFlow(ProductDetailsState())
    private val state
        get() = uiStateListener.value

    fun observeUiState() = uiStateListener.asStateFlow()

    fun fetchProductDetail(productId: Long) {
        viewModelScope.launch {
            uiStateListener.value = state.copy(loadingPage = true)
            flow { emit(mainRepository.fetchProductResponse(productId = productId)) }
                .catch {
                    debugLog { "fetch detail error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseProductDetailsResponse()
                    uiStateListener.value = if (response is ResponseEntity.Success) {

                        response.data.let { entity ->
                            entity.productDetailEntity.syncCartQuantity(localDataSource)
                            entity.productDetailEntity.syncFavoriteStatus(localDataSource)
                            entity.buyWithProductEntityList.syncCartQuantity(localDataSource)
                            entity.buyWithProductEntityList.syncFavoriteProducts(localDataSource)
                            entity.maybeLikeProductEntityList.syncCartQuantity(localDataSource)
                            entity.maybeLikeProductEntityList.syncFavoriteProducts(localDataSource)
                            entity.recommendProductEntityList.syncCartQuantity(localDataSource)
                            entity.recommendProductEntityList.syncFavoriteProducts(localDataSource)
                        }

                        val mappedData = response.data.mapToUI()

                        state.copy(
                            productDetailUI = mappedData.productDetailUI,
                            serviceUIList = mappedData.serviceUIList,
                            categoryUI = mappedData.categoryUI,
                            commentUIList = mappedData.commentUIList,
                            searchWordList = mappedData.searchWordList,
                            maybeLikeProductUIList = mappedData.maybeLikeProductUIList,
                            promotionUIList = mappedData.promotionUIList,
                            recommendProductUIList = mappedData.recommendProductUIList,
                            buyWithProductUIList = mappedData.buyWithProductUIList,
                            replacementProductsCategoryDetail = mappedData.replacementProductsCategoryDetail,
                            detailHeader = DetailHeader(1, mappedData.productDetailUI, mappedData.replacementProductsCategoryDetail),
                            detailPrices = DetailPrices(2, mappedData.productDetailUI.priceUIList),
                            error = null,
                            loadingPage = false
                        )
                    } else {
                        state.copy(loadingPage = false, error = ErrorState.Error())
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun isLoginAlready() = localDataSource.isAlreadyLogin()

    fun changeCart(productId: Long, quantity: Int, oldQuan: Int) {
        viewModelScope.launch {
            cartManager.add(id = productId, oldCount = oldQuan, newCount = quantity)
        }
    }

    fun changeFavoriteStatus(productId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            likeManager.like(productId, !isFavorite)
        }
    }

    data class ProductDetailsState(
        val productDetailUI: ProductDetailUI? = null,
        val serviceUIList: List<ServiceUI> = emptyList(),
        val categoryUI: CategoryUI? = null,
        val commentUIList: List<CommentUI> = emptyList(),
        val searchWordList: List<String> = emptyList(),
        val maybeLikeProductUIList: List<ProductUI> = emptyList(),
        val promotionUIList: List<PromotionUI> = emptyList(),
        val recommendProductUIList: List<ProductUI> = emptyList(),
        val buyWithProductUIList: List<ProductUI> = emptyList(),
        val replacementProductsCategoryDetail: CategoryDetailUI? = null,
        val detailHeader: DetailHeader? = null,
        val detailPrices: DetailPrices? = null,
        val error: ErrorState? = null,
        val loadingPage: Boolean = false
    ) : State
}