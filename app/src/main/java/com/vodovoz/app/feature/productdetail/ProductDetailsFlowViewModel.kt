package com.vodovoz.app.feature.productdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts.Companion.DISCOUNT
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
import com.vodovoz.app.feature.productdetail.viewholders.detailsearchword.inner.SearchWordItem
import com.vodovoz.app.feature.productdetail.viewholders.detailservices.DetailServices
import com.vodovoz.app.feature.productdetail.viewholders.detailslisttitles.DetailsTitle
import com.vodovoz.app.feature.productdetail.viewholders.detailtabs.DetailTabs
import com.vodovoz.app.feature.products_slider.ProductsSliderConfig
import com.vodovoz.app.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.mapper.PaginatedProductListMapper.mapToUI
import com.vodovoz.app.mapper.ProductDetailBundleMapper.mapToUI
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.CommentUI
import com.vodovoz.app.ui.model.ProductDetailUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.model.custom.PromotionsSliderBundleUI
import com.vodovoz.app.ui.model.getArticleNumber
import com.vodovoz.app.ui.model.getDeposit
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
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
    savedState: SavedStateHandle,
    private val mainRepository: MainRepository,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager,
    private val accountManager: AccountManager,
) : ViewModel() {

    private val uiStateListener = MutableStateFlow(ProductDetailsState())
    private val state
        get() = uiStateListener.value

    private val productId = savedState.get<Long>("productId")

    private val eventListener = MutableSharedFlow<ProductDetailsEvents>()
    fun observeEvent() = eventListener.asSharedFlow()

    fun observeUiState() = uiStateListener.asStateFlow()

    private val updateFabListener = MutableSharedFlow<Int>()
    fun observeUpdateFab() = updateFabListener.asSharedFlow()

    init {
        viewModelScope.launch {
            cartManager
                .observeCarts()
                .filter { it.containsKey(productId) }
                .collect {
                    val cartQuantity = it[productId] ?: return@collect
                    uiStateListener.update { productDetailsState ->
                        productDetailsState.copy(
                            cartQuantity = cartQuantity
                        )
                    }
                    updateFabListener.emit(cartQuantity)
                }
        }
    }

    private fun fetchViewedProducts() {
        viewModelScope.launch {
            val userId = accountManager.fetchAccountId() ?: return@launch
            uiStateListener.value = state.copy(loadingPage = true)
            flow {
                emit(mainRepository.fetchViewedProductsSlider(userId = userId))
            }
                .onEach { response ->
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val mappedData: CategoryDetailUI = response.data.mapToUI()
                        state.copy(
                            viewedProducts = mappedData,
                            viewedProductsTitle = DetailsTitle(
                                141,
                                name = "Вы смотрели",
                                categoryProductsName = mappedData.name,
                            ),
                            error = null,
                            loadingPage = false
                        )
                    } else {
                        state.copy(loadingPage = false, error = ErrorState.Error())
                    }
                }
                .flowOn(Dispatchers.Default)
                .catch {
                    debugLog { "fetch viewed products error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }

    }

    fun fetchProductDetail() {
        viewModelScope.launch {
            val productId = productId ?: return@launch
            uiStateListener.value = state.copy(loadingPage = true)
            flow { emit(mainRepository.fetchProductResponse(productId = productId)) }
                .onEach { response ->
                    uiStateListener.value = if (response is ResponseEntity.Success) {

                        fetchMaybeLikeProducts()

                        val mappedData = response.data.mapToUI()

                        if (mappedData.productDetailUI.brandUI != null) {
                            fetchBrandProducts(
                                mappedData.productDetailUI.id,
                                mappedData.productDetailUI.brandUI.id
                            )
                        }

                        fetchViewedProducts()
                        fetchPresentInfo()

                        state.copy(
                            buyWithProductUIList = mappedData.buyWithProductUIList,
                            commentsUI = mappedData.commentUIList.map { commentUI ->
                                commentUI.copy(
                                    forDetailPage = true
                                )
                            },
                            searchWords = mappedData.searchWordList,
                            categoryUI = mappedData.categoryUI,
                            productDetailUI = mappedData.productDetailUI,
                            articleNumber = mappedData.productDetailUI.getArticleNumber(),
                            deposit = mappedData.productDetailUI.getDeposit(),
                            detailHeader = DetailHeader(
                                1,
                                mappedData.productDetailUI,
                                mappedData.replacementProductsCategoryDetail,
                                mappedData.categoryUI.id
                            ),
                            detailPrices = DetailPrices(2, mappedData.productDetailUI.priceUIList),
                            detailBlocks = DetailBlocks(123, mappedData.productDetailUI.blockList),
                            detailServices = DetailServices(4, mappedData.serviceUIList),
                            detailTabs = DetailTabs(4, mappedData.productDetailUI),
                            detailCatAndBrand = DetailCatAndBrand(
                                5,
                                mappedData.categoryUI,
                                mappedData.productDetailUI.brandUI
                            ),
                            detailRecommendsProductsTitle = DetailsTitle(
                                141,
                                name = "Рекомендуем также",
                            ),
                            detailRecommendsProducts = HomeProducts(
                                7,
                                productsSliderConfig = ProductsSliderConfig(
                                    containShowAllButton = false,
                                    largeTitle = true
                                ),
                                items = listOf(
                                    CategoryDetailUI(
                                        name = "Рекомендуем также",
                                        productAmount = mappedData.recommendProductUIList.size,
                                        productUIList = mappedData.recommendProductUIList
                                    )
                                ),
                                productsType = DISCOUNT,
                                prodList = mappedData.recommendProductUIList
                            ),
                            detailPromotionsTitle = DetailsTitle(
                                141,
                                name = mappedData.promotionsAction?.name ?: "",
                            ),
                            detailPromotions = HomePromotions(
                                8,
                                PromotionsSliderBundleUI(
                                    title = mappedData.promotionsAction?.name ?: "",
                                    containShowAllButton = false,
                                    promotionUIList = mappedData.promotionsAction?.promotionUIList
                                        ?: emptyList(),
                                ),
                                whiteBg = true
                            ),
                            detailSearchWord = DetailSearchWord(
                                10,
                                searchWordList = mappedData.searchWordList.map {
                                    SearchWordItem(it)
                                }
                            ),
                            detailBuyWithTitle = DetailsTitle(
                                141,
                                name = "С этим товаром покупают",
                            ),
                            detailBuyWith = HomeProducts(
                                11,
                                productsSliderConfig = ProductsSliderConfig(
                                    containShowAllButton = false,
                                    largeTitle = true
                                ),
                                items = listOf(
                                    CategoryDetailUI(
                                        name = "С этим товаром покупают",
                                        productAmount = mappedData.buyWithProductUIList.size,
                                        productUIList = mappedData.buyWithProductUIList
                                    )
                                ),
                                productsType = DISCOUNT,
                                prodList = mappedData.buyWithProductUIList
                            ),
                            detailComments = DetailComments(
                                12,
                                commentUIList = mappedData.commentUIList.map { it.copy(forDetailPage = true) },
                                productId = mappedData.productDetailUI.id,
                                commentImages = mappedData.commentImages,
                                rating = mappedData.productDetailUI.rating,
                                commentsAmountText = mappedData.productDetailUI.commentsAmountText,
                            ),
                            error = null,
                            loadingPage = false
                        )
                    } else {
                        state.copy(loadingPage = false, error = ErrorState.Error())
                    }
                }
                .flowOn(Dispatchers.Default)
                .catch {
                    debugLog { "fetch detail error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    private fun fetchPresentInfo() {
        val userId = accountManager.fetchAccountId()
        if (productId != null && userId != null) {
            viewModelScope.launch {
                flow {
                    emit(
                        mainRepository.fetchPresentInfo(
                            userid = userId,
                            productId = productId,
                        )
                    )
                }.onEach { presentInfo ->
                    if (presentInfo.status == "Success") {
                        uiStateListener.value = state.copy(
                            presentInfo = presentInfo.presentInfoData
                        )
                    }
                }.catch { debugLog { "fetch present error ${it.localizedMessage}" } }.collect()
            }
        }
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
                showDetailPreviewText = !s.showDetailPreviewText
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
    }


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
        val showDetailPreviewText: Boolean = false,
        val showAllProperties: Boolean = false,
        val articleNumber: String = "",
        val deposit: Int = 0,
        val searchWords: List<String> = emptyList(),
        val cartQuantity: Int = 0,
        val buttonIsLoading: Boolean = false,
        val hideFloatingButton: Boolean = true,
    ) : State
}