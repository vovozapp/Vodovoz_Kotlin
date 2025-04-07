package com.vodovoz.app.feature.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.core.network.VodovozWebConfig
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.design_system.model.AboutAdvertisingUi
import com.vodovoz.app.design_system.model.BannerUi
import com.vodovoz.app.design_system.model.CategoryWithProductsUi
import com.vodovoz.app.design_system.model.ProductUi
import com.vodovoz.app.design_system.model.PromotionUi
import com.vodovoz.app.design_system.model.SectionUi
import com.vodovoz.app.design_system.model.SpecialPromotionUi
import com.vodovoz.app.design_system.model.StoryUi
import com.vodovoz.app.design_system.model.mapToUi
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.design_system.model.withUpdatedFavorites
import com.vodovoz.app.domain.general.model.ButtonAction
import com.vodovoz.app.domain.general.model.DataAllAction
import com.vodovoz.app.domain.general.model.VodovozAction
import com.vodovoz.app.domain.general.model.toUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.home.model.MenuItemTypeUi
import com.vodovoz.app.feature.home.model.MenuItemUi
import com.vodovoz.app.feature.home.model.OrderUi
import com.vodovoz.app.feature.home.model.OrderWithMenuUi
import com.vodovoz.app.feature.home.model.PopularCategoryUi
import com.vodovoz.app.feature.home.model.UnratedProductUi
import com.vodovoz.app.feature.home.model.UnratedProductsSectionUi
import com.vodovoz.app.feature.home.model.toUi
import com.vodovoz.app.feature.home.viewholders.homebanners.HomeBanners
import com.vodovoz.app.feature.home.viewholders.homepopulars.HomePopulars
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts
import com.vodovoz.app.feature.home.viewholders.homeproductstabs.HomeProductsTabs
import com.vodovoz.app.feature.home.viewholders.homesections.HomeSections
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle
import com.vodovoz.app.mapper.BannerMapper.mapToUI
import com.vodovoz.app.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.mapper.PopupNewsMapper.mapToUI
import com.vodovoz.app.ui.model.PopupNewsUI
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.singleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager,
    private val accountManager: AccountManager,
    private val vodovozServiceRepository: VodovozServiceRepository,
) : PagingContractViewModel<HomeFlowViewModel.HomeState, HomeFlowViewModel.HomeEvents>(HomeState.idle()) {

    init {
        listenFavorites()
    }

    private fun listenFavorites() = viewModelScope.launch {
        val uiStateFlow = uiStateListener.map { pagingState -> pagingState.data.uiState }

        uiStateFlow.combine(likeManager.observeLikes()) { uiState, favorites ->
            uiState to favorites
        }.collectLatest { (uiState, favorites) ->

            if (uiState != HomeUiState.Success) return@collectLatest

            val sectionTopDeferred =
                async(Dispatchers.Default) { dataState.sectionTop.withUpdatedFavorites(favorites) }
            val sectionBottomDeferred =
                async(Dispatchers.Default) { dataState.sectionBottom.withUpdatedFavorites(favorites) }
            val sectionViewedProductsDeferred =
                async(Dispatchers.Default) {
                    dataState.sectionViewedProducts.withUpdatedFavorites(favorites)
                }
            val sectionNewProductsDeferred =
                async(Dispatchers.Default) {
                    dataState.sectionNewProducts.withUpdatedFavorites(favorites)
                }
            val sectionHurryUpBuyProducts =
                dataState.sectionHurryUpBuyProducts.withUpdatedFavorites(favorites)


            val currentCategoryWithProducts =
                dataState.currentCategoryWithProducts.withUpdatedFavorites(favorites)

            val sectionTop = sectionTopDeferred.await()
            val sectionBottom = sectionBottomDeferred.await()
            val sectionViewedProducts = sectionViewedProductsDeferred.await()
            val sectionNewProducts = sectionNewProductsDeferred.await()


            uiStateListener.updateData { s ->
                s.copy(
                    sectionTop = sectionTop,
                    sectionBottom = sectionBottom,
                    sectionViewedProducts = sectionViewedProducts,
                    sectionNewProducts = sectionNewProducts,
                    sectionHurryUpBuyProducts = sectionHurryUpBuyProducts,
                    currentCategoryWithProducts = currentCategoryWithProducts
                )
            }

        }
    }

    private suspend fun fetchPrimaryDetails(): Boolean {
        val bannersDeferred = viewModelScope.async {
            vodovozServiceRepository.getBanners().singleResult()
        }
        val storiesDeferred = viewModelScope.async {
            vodovozServiceRepository.getStories().singleResult()
        }
        val sectionPopularCategoriesDeferred = viewModelScope.async {
            vodovozServiceRepository.getPopularCategories().singleResult()
        }
        val orderMenuDeferred = viewModelScope.async {
            vodovozServiceRepository.getOrderMenu().singleResult()
        }
        val sectionsTopAndBottomDeferred = viewModelScope.async {
            vodovozServiceRepository.getSuperTop().singleResult()
        }

        val banners = bannersDeferred.await().getOrNull()
        val stories = storiesDeferred.await().getOrNull()
        val sectionPopularCategories = sectionPopularCategoriesDeferred.await().getOrNull()
        val orderMenu = orderMenuDeferred.await().getOrNull()
        val sectionsTopAndBottom = sectionsTopAndBottomDeferred.await().getOrNull()

        if (banners != null && stories != null && sectionPopularCategories != null && orderMenu != null && sectionsTopAndBottom != null) {
            val topSection = sectionsTopAndBottom.topSection.toUi(
                mapItems = { items -> items.map { it.toUi() } }
            )

            uiStateListener.updateData { s ->
                s.copy(
                    sectionPopularCategories = sectionPopularCategories.toUi { items -> items.map { it.toUi() } },
                    sectionTop = topSection,
                    sectionBottom = sectionsTopAndBottom.bottomSection.toUi { items -> items.map { it -> it.toUi() } },
                    currentCategoryWithProducts = topSection.items.firstOrNull()
                        ?: CategoryWithProductsUi.Empty,
                    orderWithMenu = orderMenu.toUi(),
                    banners = banners.mapToUi(),
                    stories = stories.mapToUi(),
                    uiState = HomeUiState.Success,
                )
            }
        } else {
            uiStateListener.updateData { s -> s.copy(uiState = HomeUiState.NetworkError) }
            return false
        }
        return true
    }

    private suspend fun fetchSecondaryDetails(): Boolean {
        val sectionPromotionsDeferred =
            viewModelScope.async { vodovozServiceRepository.getPromotions().singleResult() }
        val sectionHurryUpBuyProductsDeferred =
            viewModelScope.async { vodovozServiceRepository.getHurryUpBuyProducts().singleResult() }
        val sectionNewProductsDeferred =
            viewModelScope.async { vodovozServiceRepository.getNewProducts().singleResult() }

        sectionPromotionsDeferred.await().onSuccess { value ->
            uiStateListener.updateData { s -> s.copy(sectionPromotions = value.toUi()) }
        }.onFailure { return false }

        sectionHurryUpBuyProductsDeferred.await().onSuccess { value ->
            uiStateListener.updateData { s -> s.copy(sectionHurryUpBuyProducts = value.toUi()) }
        }.onFailure { return false }


        sectionNewProductsDeferred.await().onSuccess { value ->
            uiStateListener.updateData { s -> s.copy(sectionNewProducts = value.toUi()) }
        }.onFailure { return false }

        return true
    }

    private suspend fun fetchOptionalDetails(): Boolean {
        val viewedProductsDeferred = viewModelScope.async {
            vodovozServiceRepository.getViewedProducts().singleResult()
        }

        val popupWindowsInfoDeferred = viewModelScope.async {
            vodovozServiceRepository.getPopupWindowInfo().singleResult()
        }

        val unratedProductsSectionDeferred = viewModelScope.async {
            vodovozServiceRepository.getUnratedProductsDetails().singleResult()
        }


        val sectionViewedProducts =
            viewedProductsDeferred.await().getOrNull()?.toUi()
        val specialPromotion =
            popupWindowsInfoDeferred.await().getOrNull()?.specialPromotion?.toUi()
        val sectionUnratedProducts = unratedProductsSectionDeferred.await().getOrNull()



        uiStateListener.updateData { s ->
            s.copy(
                sectionViewedProducts = sectionViewedProducts ?: s.sectionViewedProducts,
                specialPromotion = specialPromotion ?: s.specialPromotion,
                showSpecialPromotionBS = specialPromotion != null,
                sectionUnratedProducts = sectionUnratedProducts?.toUi() ?: s.sectionUnratedProducts,
                showUnratedProductsBS = sectionUnratedProducts != null
            )
        }

        return sectionViewedProducts != null
    }


    fun fetchHomeDetails() = viewModelScope.launch {
        uiStateListener.updateData { s -> s.copy(uiState = HomeUiState.Loading) }
        fetchPrimaryDetails()
        fetchSecondaryDetails()
        fetchOptionalDetails()
    }

    fun firstLoad() {
        fetchHomeDetails()
        if (!state.isFirstLoad) {
            //uiStateListener.value = state.copy(loadingPage = true)

            viewModelScope.launch(Dispatchers.IO) {
                //updatePopupNews()
                val tasks = firstLoadTasks()
                val start = System.currentTimeMillis()
                val result = awaitAll(*tasks).flatten()
                debugLog { "first load task ${System.currentTimeMillis() - start} result size ${result.size}" }
                val positionItemsSorted =
                    (state.data.positionItems + result).toSet().sortedBy { it.position }
                uiStateListener.value = state.copy(
                    loadingPage = false,
                    data = state.data.copy(
                        positionItems = positionItemsSorted,
                        items = positionItemsSorted.map { it.item }),
                    isFirstLoad = true,
                    error = if (result.isNotEmpty()) {
                        null
                    } else {
                        state.error
                    }
                )
                //secondLoad()
            }
        }
    }

    private fun secondLoad() {
        viewModelScope.launch(Dispatchers.IO) {
//            val userId = accountManager.fetchAccountId()
//            val tasks = secondLoadTasks(userId)
//            val start = System.currentTimeMillis()
//            val result = awaitAll(*tasks).flatten()
//            val mappedResult = if (result.isNotEmpty()) {
//                result + HomeState.fetchStaticItems()
//            } else {
//                result
//            }
//            debugLog { "second load task ${System.currentTimeMillis() - start} result size ${mappedResult.size}" }
//            val positionItemsSorted =
//                (state.data.positionItems + mappedResult).toSet().sortedBy { it.position }
            uiStateListener.value = state.copy(
                loadingPage = false,
                data = state.data.copy(
//                    positionItems = positionItemsSorted,
//                    items = positionItemsSorted.map { it.item },
                    isSecondLoad = true
                ),
//                error = if (mappedResult.isNotEmpty()) {
//                    null
//                } else {
//                    state.error
//                }
            )
        }
    }

    fun refresh() = viewModelScope.launch {
        if (dataState.uiState is HomeUiState.Loading) return@launch

        uiStateListener.updateData { s ->
            s.copy(showRefreshIndicator = true)
        }

        fetchHomeDetails().join()

        uiStateListener.updateData { s ->
            s.copy(showRefreshIndicator = false)
        }


//        if (!state.loadingPage) {
//            uiStateListener.value =
//                state.copy(
//                    loadingPage = true,
//                    data = state.data.copy(
//                        items = HomeState.idle().items,
//                        positionItems = HomeState.idle().positionItems,
//                        isSecondLoad = false
//                    ),
//                    isFirstLoad = false
//                )
//            viewModelScope.launch {
//                val userId = accountManager.fetchAccountId()
//                val tasks = firstLoadTasks() + secondLoadTasks(userId)
//                val start = System.currentTimeMillis()
//                val result = awaitAll(*tasks).flatten()
//                debugLog { "refresh load task ${System.currentTimeMillis() - start} result size ${result.size}" }
//                val mappedResult = if (result.isNotEmpty()) {
//                    result + HomeState.fetchStaticItems()
//                } else {
//                    result
//                }
//                val positionItemsSorted =
//                    (state.data.positionItems + mappedResult).toSet().sortedBy { it.position }
//                uiStateListener.value = state.copy(
//                    loadingPage = false,
//                    data = state.data.copy(
//                        positionItems = positionItemsSorted,
//                        items = positionItemsSorted.map { it.item },
//                        isSecondLoad = true
//                    ),
//                    error = if (mappedResult.isNotEmpty()) {
//                        null
//                    } else {
//                        state.error
//                    },
//                    isFirstLoad = true
//                )
//            }
//        }
    }

    private fun CoroutineScope.firstLoadTasks() = arrayOf<Deferred<List<PositionItem>>>()

    private fun CoroutineScope.secondLoadTasks(userId: Long?) = arrayOf<Deferred<List<PositionItem>>>()

    private inline fun CoroutineScope.homeRequestAsync(crossinline request: suspend () -> List<PositionItem>): Deferred<List<PositionItem>> {
        return async(Dispatchers.IO) {
            runCatching { request.invoke() }
                .onFailure { showNetworkError(it) }
                .getOrDefault(emptyList())
        }
    }

    private fun showNetworkError(throwable: Throwable) {
        val error = throwable.toErrorState()
        if (error is ErrorState.NetworkError) {
            uiStateListener.value = state.copy(error = error)
        }
    }

    private fun updatePopupNews() {
        viewModelScope.launch {
            val userId = accountManager.fetchAccountId()
            flow { emit(repository.fetchPopupNews(userId)) }
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                news = response.data.mapToUI()
                            )
                        )
                    }
                }
                .catch { debugLog { "fetch popup news error ${it.localizedMessage}" } }
                .collect()
        }
    }

    private fun updateStateByTabAndProductPositions(
        positionTab: Int,
        position: Int,
        categoryId: Long,
    ) {
        val positionItems = state.data.positionItems.map {
            when (it.position) {
                position -> {
                    it.copy(
                        item = (it.item as HomeProducts).copy(
                            prodList = it.item.items.find { it.id == categoryId }?.productUIList
                                ?: it.item.prodList
                        )
                    )
                }

                positionTab -> {
                    it.copy(
                        item = (it.item as HomeProductsTabs).copy(
                            tabsNames = it.item.tabsNames.map { cat ->
                                cat.copy(isSelected = cat.id == categoryId)
                            }
                        )
                    )
                }

                else -> {
                    it
                }
            }
        }

        uiStateListener.value = state.copy(
            data = state.data.copy(
                items = positionItems.map { it.item },
                positionItems = positionItems
            )
        )
    }

    fun updateProductsSliderByCategory(position: Int, categoryId: Long) {
        when (position) {
            POSITION_90_TAB -> updateStateByTabAndProductPositions(
                POSITION_90_TAB,
                POSITION_100,
                categoryId
            )

            POSITION_180_TAB -> updateStateByTabAndProductPositions(
                POSITION_180_TAB,
                POSITION_190,
                categoryId
            )
        }
    }

    fun onPreOrderClick(id: Long, name: String, detailPicture: String) {
        viewModelScope.launch {
            eventListener.emit(HomeEvents.GoToPreOrder(id, name, detailPicture))
        }
    }

    fun onSendCommentClick() {
        viewModelScope.launch {
            val accountId = accountManager.fetchAccountId()
            if (accountId == null) {
                eventListener.emit(HomeEvents.GoToProfile)
            } else {
                eventListener.emit(HomeEvents.SendComment)
            }
        }
    }

    fun goToProfile() {
        viewModelScope.launch {
            eventListener.emit(HomeEvents.GoToProfile)
        }
    }

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

    fun changeRating(productId: Long, rating: Float, oldRating: Float) {
        viewModelScope.launch {
            ratingProductManager.rate(productId, rating = rating, oldRating = oldRating)
        }
    }

    fun hasShown() {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                hasShow = true
            )
        )
    }

    fun repeatOrder(orderId: Long) {
        val userId =
            accountManager.fetchAccountId() ?: return
        uiStateListener.value = state.copy(loadingPage = true, error = null)
        viewModelScope.launch {
            flow {
                emit(
                    repository.repeatOrder(
                        userId = userId,
                        orderId = orderId
                    )
                )
            }.onEach { response ->
                if (response is ResponseEntity.Success) {
                    cartManager.updateCartListState(true)
                    uiStateListener.value = state.copy(loadingPage = false, error = null)
                    eventListener.emit(HomeEvents.GoToCart)
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
                    debugLog { "repeat order error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun onSectionsTabClick(title: String) {
        val positionItems = state.data.positionItems.map { positionItem ->
            when (positionItem.position) {
                POSITION_15 -> {
                    positionItem.copy(
                        item = (positionItem.item as HomeSections).copy(
                            items = positionItem.item.items.copy(
                                parentSectionDataUIList = positionItem.item.items.parentSectionDataUIList.map {
                                    it.copy(
                                        isSelected = it.title == title
                                    )
                                }
                            )
                        )
                    )
                }

                else -> {
                    positionItem
                }
            }
        }

        uiStateListener.value = state.copy(
            data = state.data.copy(
                items = positionItems.map { it.item },
                positionItems = positionItems
            )
        )
    }

    fun selectCategory(categoryWithProductsUi: CategoryWithProductsUi) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                currentCategoryWithProducts = categoryWithProductsUi
            )
        }
        eventListener.emit(HomeEvents.ScrollTopProductsToStart)
    }

    fun closeSpecialPromotionBottomSheet() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                showSpecialPromotionBS = false
            )
        }
    }

    fun navigateToStories(startStory: StoryUi) = viewModelScope.launch {
        eventListener.emit(HomeEvents.GoToStories(storyId = startStory.id))
    }

    fun navigateToPromotionDetails(promotion: PromotionUi) = viewModelScope.launch {
        eventListener.emit(HomeEvents.GoToPromotionDetails(promotionId = promotion.id))

    }

    fun navigateToProductDetails(product: ProductUi) = viewModelScope.launch {
        eventListener.emit(HomeEvents.GoToProductDetails(productId = product.id))
    }

    fun handleButtonAction(action: ButtonAction) = viewModelScope.launch {
        eventListener.emit(HomeEvents.ActivateButtonAction(action))
    }

    fun navigateToSearch() = viewModelScope.launch {
        eventListener.emit(HomeEvents.GoToSearch)
    }

    fun changeFavorite(product: ProductUi) = viewModelScope.launch {
        likeManager.changeFavorite(product.id, !product.isFavorite)
    }

    fun closeUnratedProductsBottomSheet() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                showUnratedProductsBS = false
            )
        }

        //todo - remove it
        delay(5000L)
        uiStateListener.updateData { s ->
            s.copy(
                showUnratedProductsBS = true
            )
        }
    }

    fun changeUnratedProductRating(product: UnratedProductUi, rating: Float) {
        //todo - finish method(when the product evaluation method is completed)
    }

    fun navigateToPopularCategory(popularCategory: PopularCategoryUi) = viewModelScope.launch {
        if(popularCategory.action == null){
            eventListener.emit(HomeEvents.GoToCategoryProductList(popularCategory.id))
        }else{
            eventListener.emit(HomeEvents.ActivateDataAllAction(popularCategory.action))
        }
    }

    fun showAdvertisingBottomSheet(aboutAdvertisingUi: AboutAdvertisingUi) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                currentAdvertising = aboutAdvertisingUi,
                showAdvertisingBS = true
            )
        }
    }

    fun closeAdvertisingBottomSheet() {
        uiStateListener.updateData { s ->
            s.copy(showAdvertisingBS = false)
        }
    }

    fun showSpeechRecognizer() = viewModelScope.launch {
        eventListener.emit(HomeEvents.ShowSpeechRecognizer)
    }

    fun activateBannerAction(banner: BannerUi) = viewModelScope.launch {
        eventListener.emit(HomeEvents.ActivateVodovozAction(banner.action))
    }

    fun navigateToOrderDetails(order: OrderUi) = viewModelScope.launch {
        eventListener.emit(HomeEvents.GoToOrderDetails(order.orderId))
    }

    fun navigateByMenuItem(menuItem: MenuItemUi) = viewModelScope.launch {
        val event = when(menuItem.type){
            MenuItemTypeUi.History -> HomeEvents.GoToOrdersHistory
            MenuItemTypeUi.Payment -> HomeEvents.GoToWebView(VodovozWebConfig.ABOUT_PAYMENT_URL, "")
            MenuItemTypeUi.None -> {
                //todo - show toast
                TODO()
            }
        }
        eventListener.emit(event)
    }

    data class PositionItem(
        val position: Int,
        val item: Item,
    )

    @Immutable
    sealed class HomeEvents : Event {
        data class GoToPreOrder(val id: Long, val name: String, val detailPicture: String) :
            HomeEvents()

        data object GoToSearch : HomeEvents()

        data object GoToProfile : HomeEvents()
        data object SendComment : HomeEvents()
        data object GoToCart : HomeEvents()
        data object ScrollTopProductsToStart : HomeEvents()
        data object ShowSpeechRecognizer : HomeEvents()
        data object GoToOrdersHistory: HomeEvents()

        data class GoToStories(val storyId: Long) : HomeEvents()
        data class GoToProductDetails(val productId: Long) : HomeEvents()
        data class GoToPromotionDetails(val promotionId: Long) : HomeEvents()
        data class ActivateButtonAction(val action: ButtonAction) : HomeEvents()
        data class GoToCategoryProductList(val categoryId: Long) : HomeEvents()
        data class ActivateDataAllAction(val action: DataAllAction) : HomeEvents()
        data class ActivateVodovozAction(val action: VodovozAction) : HomeEvents()
        data class GoToOrderDetails(val orderId: Int) : HomeEvents()
        data class GoToWebView(val url: String, val title: String) : HomeEvents()
    }

    @Immutable
    sealed class HomeUiState {
        data object Success : HomeUiState()
        data object Loading : HomeUiState()
        data object NetworkError : HomeUiState()
    }

    @Immutable
    data class HomeState(
        val positionItems: List<PositionItem> = emptyList(),
        val items: List<Item> = emptyList(),
        val news: PopupNewsUI? = null,
        val hasShow: Boolean = false,
        val isSecondLoad: Boolean = false,

        val banners: List<BannerUi> = emptyList(),
        val stories: List<StoryUi> = emptyList(),
        val orderWithMenu: OrderWithMenuUi = OrderWithMenuUi.Empty,

        val sectionPromotions: SectionUi<PromotionUi> = SectionUi.empty(),
        val sectionPopularCategories: SectionUi<PopularCategoryUi> = SectionUi.empty(),
        val sectionNewProducts: SectionUi<ProductUi> = SectionUi.empty(),
        val sectionHurryUpBuyProducts: SectionUi<ProductUi> = SectionUi.empty(),
        val sectionTop: SectionUi<CategoryWithProductsUi> = SectionUi.empty(),
        val currentCategoryWithProducts: CategoryWithProductsUi = CategoryWithProductsUi.Empty,
        val sectionBottom: SectionUi<CategoryWithProductsUi> = SectionUi.empty(),
        val sectionViewedProducts: SectionUi<ProductUi> = SectionUi.empty(),
        val sectionUnratedProducts: UnratedProductsSectionUi = UnratedProductsSectionUi.Empty,
        val specialPromotion: SpecialPromotionUi = SpecialPromotionUi.Empty,
        val currentAdvertising: AboutAdvertisingUi = AboutAdvertisingUi.Empty,

        val uiState: HomeUiState = HomeUiState.Success,
        val showSpecialPromotionBS: Boolean = false,
        val showUnratedProductsBS: Boolean = false,
        val showAdvertisingBS: Boolean = false,
        val showRefreshIndicator: Boolean = false,
    ) : State {
        companion object {
            fun idle(): HomeState {

                return HomeState(
                    positionItems = emptyList(),
                    items = emptyList()
                )
            }

        }
    }

    companion object {
        const val POSITION_10 = 10
        const val POSITION_15 = 15
        const val POSITION_20_TITLE = 20
        const val POSITION_30 = 30
        const val POSITION_40_TITLE = 40
        const val POSITION_50 = 50
        const val POSITION_60_TITLE = 60
        const val POSITION_70 = 70
        const val POSITION_80 = 80
        const val POSITION_90_TAB = 90
        const val POSITION_100 = 100
        const val POSITION_110_TITLE = 110
        const val POSITION_120 = 120
        const val POSITION_130 = 130
        const val POSITION_140_TITLE = 140
        const val POSITION_150 = 150
        const val POSITION_160_TITLE = 160
        const val POSITION_170 = 170
        const val POSITION_180_TAB = 180
        const val POSITION_190 = 190
        const val POSITION_200_TITLE = 200
        const val POSITION_210 = 210
        const val POSITION_220 = 220
        const val POSITION_230_TITLE = 230
        const val POSITION_240 = 240
        const val POSITION_250_TITLE = 250
        const val POSITION_260 = 260
        const val POSITION_270 = 270
    }
}