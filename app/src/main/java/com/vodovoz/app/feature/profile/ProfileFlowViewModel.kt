package com.vodovoz.app.feature.profile

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.order.OrderSliderResponseJsonParser.parseOrderSliderResponse
import com.vodovoz.app.data.parser.response.user.PersonalProductsJsonParser.parsePersonalProductsResponse
import com.vodovoz.app.data.parser.response.user.UserDataResponseJsonParser.parseUserDataResponse
import com.vodovoz.app.data.parser.response.viewed.ViewedProductSliderResponseJsonParser.parseViewedProductsSliderResponse
import com.vodovoz.app.feature.favorite.mapper.FavoritesMapper
import com.vodovoz.app.feature.home.HomeFlowViewModel
import com.vodovoz.app.feature.home.viewholders.homeorders.HomeOrders
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts
import com.vodovoz.app.feature.profile.viewholders.models.*
import com.vodovoz.app.feature.sitestate.SiteStateManager
import com.vodovoz.app.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.mapper.OrderMapper.mapToUI
import com.vodovoz.app.mapper.UserDataMapper.mapToUI
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val localDataSource: LocalDataSource,
    private val dataRepository: DataRepository,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager,
    private val accountManager: AccountManager,
    private val siteStateManager: SiteStateManager,
    private val tabManager: TabManager
) : PagingContractViewModel<ProfileFlowViewModel.ProfileState, ProfileFlowViewModel.ProfileEvents>(
    ProfileState.idle()
) {

    init {
        viewModelScope.launch {
            siteStateManager.requestSiteState()
        }
    }

    private fun loadPage() {
        fetchProfileData()
        fetchOrdersSlider()
        fetchViewedProductsSlider()
        fetchPersonalProducts()
        fetchProfileCategories()
    }

    private fun fetchProfileData() {
        uiStateListener.value = state.copy(loadingPage = true)
        val userId = dataRepository.fetchUserId()
        if (userId == null) {
            uiStateListener.value = state.copy(data = state.data.copy(isLogin = false))
            return
        }
        viewModelScope.launch {
            flow { emit(repository.fetchUserData(userId)) }
                .catch {
                    debugLog { "fetch profile data error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(
                            error = it.toErrorState(),
                            loadingPage = false,
                            data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_1,
                                    null
                                )
                            )
                        )
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseUserDataResponse()
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val item = PositionItem(
                            POSITION_1,
                            ProfileHeader(data = response.data.mapToUI())
                        )
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = state.data.items + item),
                            error = null
                        )
                    } else {
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_1,
                                    null
                                )
                            )
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchOrdersSlider() {
        uiStateListener.value = state.copy(loadingPage = true)
        val userId = dataRepository.fetchUserId()
        if (userId == null) {
            uiStateListener.value = state.copy(data = state.data.copy(isLogin = false))
            return
        }
        viewModelScope.launch {
            flow { emit(repository.fetchOrdersSlider(userId)) }
                .catch {
                    debugLog { "fetch profile orders slider error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(
                            error = it.toErrorState(),
                            loadingPage = false,
                            data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_2,
                                    null
                                )
                            )
                        )
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseOrderSliderResponse()
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val item = PositionItem(
                            POSITION_2,
                            ProfileOrders(data = response.data.mapToUI())
                        )
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = state.data.items + item),
                            error = null
                        )
                    } else {
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_2,
                                    null
                                )
                            )
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchProfileCategories() {
        uiStateListener.value = state.copy(loadingPage = true)
        val userId = dataRepository.fetchUserId()
        if (userId == null) {
            uiStateListener.value = state.copy(data = state.data.copy(isLogin = false))
            return
        }
        viewModelScope.launch {
            flow { emit(repository.fetchProfileCategories(userId)) }
                .catch {
                    debugLog { "fetch profile categories error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(
                            error = it.toErrorState(),
                            loadingPage = false,
                            data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_3,
                                    null
                                )
                            )
                        )
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    uiStateListener.value = if (it.status != null && it.status == "Success") {
                        val list = it.fetchProfileCategoryUIList()
                        val item = PositionItem(
                            POSITION_3,
                            ProfileMain(items = list)
                        )

                        var amount: Int? = null

                        list?.forEach {
                            it.insideCategories?.forEach {cat ->
                                if (cat.amount != null) {
                                    amount = cat.amount
                                }
                            }
                        }

                        tabManager.saveBottomNavProfileState(amount)

                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = state.data.items + item),
                            error = null
                        )
                    } else {
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_3,
                                    null
                                )
                            )
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchViewedProductsSlider() {
        uiStateListener.value = state.copy(loadingPage = true)
        val userId = dataRepository.fetchUserId()
        if (userId == null) {
            uiStateListener.value = state.copy(data = state.data.copy(isLogin = false))
            return
        }
        viewModelScope.launch {
            flow { emit(repository.fetchViewedProductsSlider(userId)) }
                .catch {
                    debugLog { "fetch profile viewed products error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(
                            error = it.toErrorState(),
                            loadingPage = false,
                            data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_5,
                                    null
                                )
                            )
                        )
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseViewedProductsSliderResponse()
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val item = PositionItem(
                            POSITION_5,
                            HomeProducts(
                                14, response.data.mapToUI(), ProductsSliderConfig(
                                    containShowAllButton = false
                                ), HomeProducts.VIEWED
                            )
                        )
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = state.data.items + item),
                            error = null
                        )
                    } else {
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_5,
                                    null
                                )
                            )
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchPersonalProducts() {
        uiStateListener.value = state.copy(loadingPage = true)
        val userId = dataRepository.fetchUserId()
        if (userId == null) {
            uiStateListener.value = state.copy(data = state.data.copy(isLogin = false))
            return
        }
        viewModelScope.launch {
            flow { emit(repository.fetchPersonalProducts(userId, 1)) }
                .catch {
                    debugLog { "fetch profile personal products error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(
                            error = it.toErrorState(),
                            loadingPage = false,
                            data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_6,
                                    null
                                )
                            )
                        )
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parsePersonalProductsResponse()
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        val item = PositionItem(
                            POSITION_6,
                            ProfileBestForYou(data = data.copy(
                                productUIList = FavoritesMapper.mapFavoritesListByManager("grid", data.productUIList)
                            ))
                        )
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = state.data.items + item),
                            error = null
                        )
                    } else {
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_6,
                                    null
                                )
                            )
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun firstLoad() {
        if (!state.isFirstLoad) {
            uiStateListener.value =
                state.copy(isFirstLoad = true, loadingPage = true)
            loadPage()
        }
    }

    fun refreshIdle() {
        uiStateListener.value =
            state.copy(loadingPage = true, bottomItem = null, data = state.data.copy(items = ProfileState.idle().items, isLogin = true))
        loadPage()
    }

    fun refresh() {
        uiStateListener.value =
            state.copy(loadingPage = true, bottomItem = null)
        loadPage()
    }

    fun logout() {
        dataRepository.logout().subscribe()
        viewModelScope.launch {
            tabManager.clearBottomNavProfileState()
            cartManager.clearCart()
            accountManager.removeUserId()
            eventListener.emit(ProfileEvents.Logout)
        }
    }

    fun checkLogin() {
        viewModelScope.launch {
            uiStateListener.value = state.copy(data = state.data.copy(isLogin = isLoginAlready()))
        }
    }

    fun isLoginAlready() = dataRepository.isAlreadyLogin()

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

    data class ProfileState(
        val items: List<PositionItem>,
        val isLogin: Boolean = true
    ) : State {
        companion object {
            fun idle(): ProfileState {
                return ProfileState(
                    listOf(
                        PositionItem(
                            POSITION_4,
                            ProfileLogout()
                        ),
                    )
                )
            }
        }
    }

    sealed class ProfileEvents : Event {
        object Logout : ProfileEvents()
    }

    data class PositionItem(
        val position: Int,
        val item: Item?
    )

    companion object {
        const val POSITION_1 = 1
        const val POSITION_2 = 2
        const val POSITION_3 = 3
        const val POSITION_4 = 4
        const val POSITION_5 = 5
        const val POSITION_6 = 6

        const val POSITIONS_COUNT = 6
    }
}