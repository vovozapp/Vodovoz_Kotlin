package com.vodovoz.app.feature.productdetail

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.about_product.AboutProductManager
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.design_system.model.CommentUi
import com.vodovoz.app.design_system.model.ProductDetailsButtonsUi
import com.vodovoz.app.design_system.model.ProductDetailsTabUi
import com.vodovoz.app.design_system.model.ProductDetailsUi
import com.vodovoz.app.design_system.model.mapToUi
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.home.model.ProductUi
import com.vodovoz.app.feature.home.model.SectionUi
import com.vodovoz.app.feature.home.model.toUi
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts
import com.vodovoz.app.feature.home.viewholders.homepromotions.HomePromotions
import com.vodovoz.app.feature.productdetail.present.model.PresentInfoData
import com.vodovoz.app.feature.productdetail.viewholders.detailblocks.DetailBlocks
import com.vodovoz.app.feature.productdetail.viewholders.detailbrandproductlist.DetailBrandList
import com.vodovoz.app.feature.productdetail.viewholders.detailcatandbrand.DetailCatAndBrand
import com.vodovoz.app.feature.productdetail.viewholders.detailcomments.DetailComments
import com.vodovoz.app.feature.productdetail.viewholders.detailheader.DetailHeader
import com.vodovoz.app.feature.productdetail.viewholders.detailprices.DetailPrices
import com.vodovoz.app.feature.productdetail.viewholders.detailproductmaybelike.DetailMaybeLike
import com.vodovoz.app.feature.productdetail.viewholders.detailsearchword.DetailSearchWord
import com.vodovoz.app.feature.productdetail.viewholders.detailservices.DetailServices
import com.vodovoz.app.feature.productdetail.viewholders.detailslisttitles.DetailsTitle
import com.vodovoz.app.feature.productdetail.viewholders.detailtabs.DetailTabs
import com.vodovoz.app.mapper.PaginatedProductListMapper.mapToUI
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.CommentUI
import com.vodovoz.app.ui.model.ProductDetailUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class ProductDetailsFlowViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager,
    private val accountManager: AccountManager,
    private val vodovozServiceRepository: VodovozServiceRepository,
    private val aboutProductManager: AboutProductManager,
) : ViewModel() {

    private val uiStateListener = MutableStateFlow(ProductDetailsState())
    private val state
        get() = uiStateListener.value


    private val eventListener = MutableSharedFlow<ProductDetailsEvents>()
    fun observeEvent() = eventListener.asSharedFlow()

    fun observeUiState() = uiStateListener.asStateFlow()

    private val updateFabListener = MutableSharedFlow<Int>()
    fun observeUpdateFab() = updateFabListener.asSharedFlow()

    init {
        viewModelScope.launch {
            cartManager
                .observeCarts()
                .collectLatest { cartMap ->
                    val cartQuantity = cartMap.getOrDefault(state.productDetails.id, 0)
                    uiStateListener.update { productDetailsState ->
                        productDetailsState.copy(
                            cartQuantity = cartQuantity
                        )
                    }
                    updateFabListener.emit(cartQuantity)
                }
        }
    }

    fun fetchProductDetail() {
        viewModelScope.launch {
            vodovozServiceRepository.getProductDetails(state.productDetails.id)
                .onEach { productDetailsScreenResult ->
                    productDetailsScreenResult.onSuccess { productDetailsScreenModel ->
                        val moreProducts = productDetailsScreenModel.moreProducts

                        uiStateListener.update { s ->
                            s.copy(
                                comments = productDetailsScreenModel.comments.mapToUi(),
                                productDetails = productDetailsScreenModel.productDetails.toUi(),
                                sectionAccessory = moreProducts.sectionAccessory.toUi { list -> list.map { productModel -> productModel.toUi() } },
                                sectionSimilarProducts = moreProducts.sectionSimilar.toUi { list -> list.map { productModel -> productModel.toUi() } },
                                buttons = productDetailsScreenModel.buttons.toUi(),
                                tabs = productDetailsScreenModel.tabs.map { it.toUi() },
                                uiState = UiState.Success
                            )
                        }

                        val productDetails = state.productDetails
                        aboutProductManager.updateInfo(
                            tabs = state.tabs,
                            characteristicBlockList = productDetails.characteristics,
                            documents = productDetails.documents,
                            fullDescription = productDetails.detailInfo
                        )

                    }.onFailure {
                        uiStateListener.update { s ->
                            s.copy(
                                uiState = UiState.ProductNotFound
                            )
                        }
                    }

                }.collect()
        }
    }

    private fun fetchPresentInfo() {

    }

    private fun fetchBrandProducts(productId: Long, brandId: Long) {
        viewModelScope.launch {
            flow {
                emit(
                    mainRepository.fetchProductsByBrandResponse(
                        productId = productId,
                        brandId = brandId,
                        page = state.detailBrandList.pageIndex
                    )
                )
            }
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        uiStateListener.value = state.copy(
                            detailBrandList = state.detailBrandList.copy(
                                productUiList = response.data.mapToUI().productUIList,
                                pageAmount = if (!state.detailBrandList.loadMore) {
                                    response.data.pageAmount
                                } else {
                                    if (state.detailBrandList.pageIndex == state.detailBrandList.pageAmount) {
                                        1
                                    } else {
                                        state.detailBrandList.pageAmount
                                    }
                                }
                            ),
                            error = null,
                            loadingPage = false
                        )
                    } else {
                        uiStateListener.value = state.copy(
                            loadingPage = false
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .catch { debugLog { "fetch brands error ${it.localizedMessage}" } }
                .collect()
        }
    }

    private fun fetchMaybeLikeProducts() {
        viewModelScope.launch {
            flow { emit(mainRepository.fetchMaybeLikeProductsResponse(page = state.detailMaybeLikeProducts.pageIndex)) }
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        uiStateListener.value = state.copy(
                            detailMaybeLikeProducts = state.detailMaybeLikeProducts.copy(
                                productUiList = response.data.mapToUI().productUIList.map { pr ->
                                    pr.copy(
                                        linear = false
                                    )
                                },
                                pageAmount = if (!state.detailMaybeLikeProducts.loadMore) {
                                    response.data.pageAmount
                                } else {
                                    if (state.detailMaybeLikeProducts.pageIndex == state.detailMaybeLikeProducts.pageAmount) {
                                        1
                                    } else {
                                        state.detailMaybeLikeProducts.pageAmount
                                    }
                                }
                            ),
                            error = null,
                            loadingPage = false
                        )
                    } else {
                        uiStateListener.value = state.copy(
                            loadingPage = false
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .catch { debugLog { "fetch maybe like products error ${it.localizedMessage}" } }
                .collect()
        }
    }

    fun nextPageMaybeLikeProducts() {
        uiStateListener.value = state.copy(loadingPage = true)
        val newPage = state.detailMaybeLikeProducts.pageIndex + 1
        if (newPage > state.detailMaybeLikeProducts.pageAmount) {
            uiStateListener.value = state.copy(
                detailMaybeLikeProducts = state.detailMaybeLikeProducts.copy(
                    pageAmount = 1,
                    pageIndex = 1
                ),
                loadingPage = false
            )
        } else {
            uiStateListener.value = state.copy(
                detailMaybeLikeProducts = state.detailMaybeLikeProducts.copy(
                    pageIndex = newPage,
                    loadMore = true
                )
            )
            fetchMaybeLikeProducts()
        }
    }

    fun nextPageBrandProducts() {
        uiStateListener.value = state.copy(loadingPage = true)
        val brandId = state.productDetailUI?.brandUI?.id
        val productId = state.productDetailUI?.id
        if (brandId != null && productId != null) {
            val newPage = state.detailBrandList.pageIndex + 1
            if (newPage > state.detailBrandList.pageAmount) {
                uiStateListener.value = state.copy(
                    detailBrandList = state.detailBrandList.copy(pageAmount = 1, pageIndex = 1),
                    loadingPage = false
                )
            } else {
                uiStateListener.value = state.copy(
                    detailBrandList = state.detailBrandList.copy(
                        pageIndex = newPage,
                        loadMore = true
                    )
                )
                fetchBrandProducts(productId, brandId)
            }
        } else {
            uiStateListener.value = state.copy(loadingPage = false)
        }
    }

    fun isLoginAlready() = accountManager.isAlreadyLogin()

    fun incrementCart() {
        val productDetails = state.productDetails
        changeCart(productDetails.id, state.cartQuantity + 1, state.cartQuantity)
    }

    fun decrementCart() {
        val productDetails = state.productDetails
        changeCart(productDetails.id, state.cartQuantity - 1, state.cartQuantity)
    }


    fun changeCart(productId: Long, quantity: Int, oldQuan: Int) {
        viewModelScope.launch {
            uiStateListener.update { s ->
                s.copy(buttonIsLoading = true)
            }
            cartManager.add(id = productId, oldCount = oldQuan, newCount = quantity)
            fetchPresentInfo()
            uiStateListener.update { s ->
                s.copy(buttonIsLoading = false)
            }
        }
    }

    fun changeCart(productId: String, giftId: String) {
        viewModelScope.launch {
            uiStateListener.update { s ->
                s.copy(buttonIsLoading = true)
            }
            val (id, count) = productId.trim().split("-")
            cartManager.addWithGift(
                id = id.toLong(),
                newCount = count.toInt(),
                giftId = giftId
            )
            fetchPresentInfo()
            uiStateListener.update { s ->
                s.copy(buttonIsLoading = false)
            }
        }
    }


    private val mutex = Mutex()

    fun changeFavoriteStatus(productId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            mutex.withLock {
                likeManager.like(productId, !isFavorite)
            }
        }
    }

    fun changeRating(productId: Long, rating: Float, oldRating: Float) {
        viewModelScope.launch {
            ratingProductManager.rate(productId, rating = rating, oldRating = oldRating)
        }
    }

    fun onPreOrderClick(id: Long, name: String, detailPicture: String) {
        viewModelScope.launch {
            val accountId = accountManager.fetchAccountId()
            if (accountId == null) {
                //     eventListener.emit(ProductDetailsEvents.GoToProfile)
                eventListener.emit(ProductDetailsEvents.GoToPreOrder(id, name, detailPicture))
            } else {
                eventListener.emit(ProductDetailsEvents.GoToPreOrder(id, name, detailPicture))
            }
        }
    }

    fun onSendCommentClick(id: Long) {
        viewModelScope.launch {
            val accountId = accountManager.fetchAccountId()
            if (accountId == null) {
                eventListener.emit(ProductDetailsEvents.GoToProfile)
            } else {
                eventListener.emit(ProductDetailsEvents.SendComment(id))
            }
        }
    }

    fun onPresentInfoClick() {
        viewModelScope.launch {
            val goToCart = state.presentInfo?.moveTo == "korzina"
            if (goToCart) {
                eventListener.emit(ProductDetailsEvents.GoToCart)
            } else {
                eventListener.emit(
                    ProductDetailsEvents.GoToPresentInfo(
                        presentText = state.presentInfo?.text ?: "",
                        progress = state.presentInfo?.progress ?: 0,
                        showText = state.presentInfo?.showProgressText ?: false,
                        progressBackground = state.presentInfo?.progressBackground ?: "",
                    )
                )
            }
        }
    }

    fun showOrHideDetailText() = viewModelScope.launch {
        uiStateListener.update { s ->
            s.copy(
                showDetailText = !s.showDetailText
            )
        }
    }

    fun showAllProperties() = viewModelScope.launch {
        uiStateListener.update { s ->
            s.copy(
                showAllProperties = true
            )
        }
    }

    fun changeFloatingButton(show: Boolean) = viewModelScope.launch {
        uiStateListener.update { s ->
            s.copy(
                hideFloatingButton = show
            )
        }
    }

    fun hideMultiBottomSheet() = viewModelScope.launch {
        uiStateListener.update { s ->
            s.copy(
                showMultiBottomSheet = false
            )
        }
    }

    fun showMultiBottomSheet() = viewModelScope.launch {
        uiStateListener.update { s ->
            s.copy(
                showMultiBottomSheet = true
            )
        }
    }

    fun showPresentBottomSheet() = viewModelScope.launch {
        uiStateListener.update { s ->
            s.copy(
                showPresentBottomSheet = true
            )
        }
    }

    fun hidePresentBottomSheet() = viewModelScope.launch {
        uiStateListener.update { s ->
            s.copy(
                showPresentBottomSheet = false
            )
        }
    }

    fun hidePresentBlockBottomSheet() = viewModelScope.launch {
        uiStateListener.update { s ->
            s.copy(
                showPresentBlockBottomSheet = false
            )
        }
    }

    fun showPresentBlockBottomSheet() = viewModelScope.launch {
        uiStateListener.update { s ->
            s.copy(
                showPresentBlockBottomSheet = true
            )
        }
    }

    fun navigateToAboutProduct() = viewModelScope.launch {
        eventListener.emit(ProductDetailsEvents.GoToAboutProduct)
    }

    fun loadProductDetails(productId: Long) = viewModelScope.launch {
        uiStateListener.update { s ->
            s.copy(
                productDetails = s.productDetails.copy(id = productId),
                uiState = UiState.Loading
            )
        }
        fetchProductDetail()
    }


    sealed class ProductDetailsEvents : Event {
        data class GoToPreOrder(val id: Long, val name: String, val detailPicture: String) :
            ProductDetailsEvents()

        data object GoToProfile : ProductDetailsEvents()
        data class SendComment(val id: Long) : ProductDetailsEvents()
        data class GoToPresentInfo(
            val presentText: String,
            val progress: Int,
            val progressBackground: String,
            val showText: Boolean,
        ) : ProductDetailsEvents()

        data object GoToCart : ProductDetailsEvents()
        data object GoToAboutProduct : ProductDetailsEvents()
    }


    @Immutable
    data class ProductDetailsState(
        val productDetailUI: ProductDetailUI? = null,
        val detailHeader: DetailHeader? = null,
        val detailPrices: DetailPrices? = null,
        val detailBlocks: DetailBlocks? = null,
        val detailServices: DetailServices? = null,
        val detailTabs: DetailTabs? = null,
        val detailCatAndBrand: DetailCatAndBrand? = null,
        val detailBrandList: DetailBrandList = DetailBrandList(6),
        val detailMaybeLikeProducts: DetailMaybeLike = DetailMaybeLike(9),
        val detailRecommendsProductsTitle: DetailsTitle? = null,
        val detailRecommendsProducts: HomeProducts? = null,
        val detailPromotionsTitle: DetailsTitle? = null,
        val detailPromotions: HomePromotions? = null,
        val detailSearchWord: DetailSearchWord? = null,
        val detailBuyWithTitle: DetailsTitle? = null,
        val detailBuyWith: HomeProducts? = null,
        val detailComments: DetailComments? = null,
        val viewedProductsTitle: DetailsTitle? = null,
        val viewedProducts: CategoryDetailUI? = null,
        val presentInfo: PresentInfoData? = null,
        val error: ErrorState? = null,
        val loadingPage: Boolean = false,

        val categoryUI: CategoryUI = CategoryUI(name = ""),
        val commentsUI: List<CommentUI> = emptyList(),
        val buyWithProductUIList: List<ProductUI> = emptyList(),
        val showDetailText: Boolean = false,
        val showAllProperties: Boolean = false,
        val articleNumber: String = "",
        val deposit: Int = 0,
        val searchWords: List<String> = emptyList(),
        val cartQuantity: Int = 0,
        val buttonIsLoading: Boolean = false,
        val hideFloatingButton: Boolean = true,

        val productDetails: ProductDetailsUi = ProductDetailsUi.Empty,
        val comments: List<CommentUi> = emptyList(),
        val buttons: ProductDetailsButtonsUi = ProductDetailsButtonsUi.Empty,
        val tabs: List<ProductDetailsTabUi> = emptyList(),
        val sectionSimilarProducts: SectionUi<ProductUi> = SectionUi.empty(),
        val sectionAccessory: SectionUi<ProductUi> = SectionUi.empty(),
        val uiState: UiState = UiState.Loading,

        val showMultiBottomSheet: Boolean = false,
        val showPresentBottomSheet: Boolean = false,
        val showPresentBlockBottomSheet: Boolean = false,
    ) : State {
    }

    sealed class UiState {
        data object Loading : UiState()
        data object Success : UiState()
        data object ProductNotFound : UiState()
    }
}