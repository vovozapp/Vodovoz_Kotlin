package com.vodovoz.app.feature.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ErrorState
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
import com.vodovoz.app.data.parser.response.paginatedProducts.MaybeLikeProductsResponseJsonParser.parseMaybeLikeProductsResponse
import com.vodovoz.app.data.parser.response.paginatedProducts.SomeProductsByBrandResponseJsonParser.parseSomeProductsByBrandResponse
import com.vodovoz.app.data.parser.response.product.ProductDetailsResponseJsonParser.parseProductDetailsResponse
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts.Companion.DISCOUNT
import com.vodovoz.app.feature.home.viewholders.homepromotions.HomePromotions
import com.vodovoz.app.feature.productdetail.viewholders.detailbrandproductlist.DetailBrandList
import com.vodovoz.app.feature.productdetail.viewholders.detailcatandbrand.DetailCatAndBrand
import com.vodovoz.app.feature.productdetail.viewholders.detailheader.DetailHeader
import com.vodovoz.app.feature.productdetail.viewholders.detailprices.DetailPrices
import com.vodovoz.app.feature.productdetail.viewholders.detailproductmaybelike.DetailMaybeLike
import com.vodovoz.app.feature.productdetail.viewholders.detailsearchword.DetailSearchWord
import com.vodovoz.app.feature.productdetail.viewholders.detailsearchword.inner.SearchWordItem
import com.vodovoz.app.feature.productdetail.viewholders.detailservices.DetailServices
import com.vodovoz.app.feature.productdetail.viewholders.detailtabs.DetailTabs
import com.vodovoz.app.mapper.PaginatedProductListMapper.mapToUI
import com.vodovoz.app.mapper.ProductDetailBundleMapper.mapToUI
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.model.*
import com.vodovoz.app.ui.model.custom.PromotionsSliderBundleUI
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

                        fetchMaybeLikeProducts()

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

                        if (mappedData.productDetailUI.brandUI != null) {
                            fetchBrandProducts(mappedData.productDetailUI.id, mappedData.productDetailUI.brandUI.id)
                        }

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
                            detailServices = DetailServices(3, mappedData.serviceUIList),
                            detailTabs = DetailTabs(4, mappedData.productDetailUI),
                            detailCatAndBrand = DetailCatAndBrand(5, mappedData.categoryUI, mappedData.productDetailUI.brandUI),
                            detailRecommendsProducts = HomeProducts(
                                7,
                                productsSliderConfig = ProductsSliderConfig(containShowAllButton = false, largeTitle = true),
                                items = listOf(CategoryDetailUI(
                                    name = "Рекомендуем также",
                                    productAmount = mappedData.recommendProductUIList.size,
                                    productUIList = mappedData.recommendProductUIList
                                )),
                                productsType = DISCOUNT
                            ),
                            detailPromotions = HomePromotions(
                                8,
                                PromotionsSliderBundleUI(
                                    "Товар учавствующий в акции",
                                    containShowAllButton = false,
                                    promotionUIList = mappedData.promotionUIList
                                )
                            ),
                            detailSearchWord = DetailSearchWord(
                                10,
                                searchWordList = mappedData.searchWordList.map {
                                    SearchWordItem(it)
                                }
                            ),
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

    private fun fetchBrandProducts(productId: Long, brandId: Long) {
        viewModelScope.launch {
            flow { emit(mainRepository.fetchProductsByBrandResponse(productId = productId, brandId = brandId, page = state.detailBrandList.pageIndex)) }
                .catch { debugLog { "fetch brands error ${it.localizedMessage}" } }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseSomeProductsByBrandResponse()
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
                .collect()
        }
    }

    private fun fetchMaybeLikeProducts() {
        viewModelScope.launch {
            flow { emit(mainRepository.fetchMaybeLikeProductsResponse(page = state.detailMaybeLikeProducts.pageIndex)) }
                .catch { debugLog { "fetch maybe like products error ${it.localizedMessage}" } }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseMaybeLikeProductsResponse()
                    if (response is ResponseEntity.Success) {
                        uiStateListener.value = state.copy(
                            detailMaybeLikeProducts = state.detailMaybeLikeProducts.copy(
                                productUiList = response.data.mapToUI().productUIList.map { pr -> pr.copy(linear = false) },
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
                .collect()
        }
    }

    fun nextPageMaybeLikeProducts() {
        val newPage = state.detailMaybeLikeProducts.pageIndex + 1
        if (newPage > state.detailMaybeLikeProducts.pageAmount) {
            uiStateListener.value = state.copy(
                detailMaybeLikeProducts = state.detailMaybeLikeProducts.copy(pageAmount = 1, pageIndex = 1)
            )
        } else {
            uiStateListener.value = state.copy(
                detailMaybeLikeProducts = state.detailMaybeLikeProducts.copy(pageIndex = newPage, loadMore = true)
            )
            fetchMaybeLikeProducts()
        }
    }

    fun nextPageBrandProducts() {
        val brandId = state.productDetailUI?.brandUI?.id
        val productId = state.productDetailUI?.id
        if (brandId != null && productId != null) {
            val newPage = state.detailBrandList.pageIndex + 1
            if (newPage > state.detailBrandList.pageAmount) {
                uiStateListener.value = state.copy(
                    detailBrandList = state.detailBrandList.copy(pageAmount = 1, pageIndex = 1)
                )
            } else {
                uiStateListener.value =  state.copy(
                    detailBrandList = state.detailBrandList.copy(pageIndex = newPage, loadMore = true)
                )
                fetchBrandProducts(productId, brandId)
            }
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
        val detailServices: DetailServices? = null,
        val detailTabs: DetailTabs? = null,
        val detailCatAndBrand: DetailCatAndBrand? = null,
        val detailBrandList: DetailBrandList = DetailBrandList(6),
        val detailMaybeLikeProducts: DetailMaybeLike = DetailMaybeLike(9),
        val detailRecommendsProducts: HomeProducts? = null,
        val detailPromotions: HomePromotions? = null,
        val detailSearchWord: DetailSearchWord? = null,
        val error: ErrorState? = null,
        val loadingPage: Boolean = false
    ) : State
}