package com.vodovoz.app.feature.profile

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.*
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.user.UserDataResponseJsonParser.parseUserDataResponse
import com.vodovoz.app.feature.profile.ProfileFlowViewModel.ProfileState.Companion.fetchStaticItems
import com.vodovoz.app.feature.profile.viewholders.models.ProfileHeader
import com.vodovoz.app.feature.profile.viewholders.models.ProfileLogout
import com.vodovoz.app.feature.sitestate.SiteStateManager
import com.vodovoz.app.mapper.UserDataMapper.mapToUI
import com.vodovoz.app.ui.extensions.ContextExtensions.isTablet
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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
    private val tabManager: TabManager,
    private val application: Application,
) : PagingContractViewModel<ProfileFlowViewModel.ProfileState, ProfileFlowViewModel.ProfileEvents>(
    ProfileState.idle()
) {

    init {
        viewModelScope.launch {
            siteStateManager.requestSiteState()
        }
    }

    fun fetchFirstUserData() {
        viewModelScope.launch {
            val userId = dataRepository.fetchUserId()
            if (userId == null) {
                uiStateListener.value =
                    state.copy(data = state.data.copy(isLogin = false), loadingPage = false)
                return@launch
            }
            flow { emit(repository.fetchUserData(userId)) }
                .flowOn(Dispatchers.IO)
                .catch {
                    debugLog { "fetch user data error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .onEach {
                    val response = it.parseUserDataResponse()
                    if (response is ResponseEntity.Success) {
                        firstLoad()
                    } else {
                       logout()
                    }
                }
                .collect()
        }
    }

    private fun CoroutineScope.firstLoadTasks(userId: Long) = arrayOf(
        async(Dispatchers.IO) { fetchProfileData(POSITION_1, userId) },
        async(Dispatchers.IO) { fetchProfileCategories(POSITION_2, POSITION_4, userId) },
        async(Dispatchers.IO) { fetchOrdersSlider(POSITION_3, userId) }
    )

    private fun CoroutineScope.secondLoadTasks(userId: Long) = arrayOf(
        async(Dispatchers.IO) { fetchViewedProductsSlider(POSITION_6_TITLE, POSITION_7, userId) },
        async(Dispatchers.IO) { fetchPersonalProducts(POSITION_8, userId) },
    )

    private suspend fun fetchProfileData(position: Int, userId: Long): List<PositionItem> {
        return runCatching { repository.fetchUserData(userId, position) }
            .onFailure { showNetworkError(it) }
            .getOrDefault(emptyList())
    }

    private suspend fun fetchOrdersSlider(position: Int, userId: Long): List<PositionItem> {
        return runCatching { repository.fetchOrdersSliderProfile(userId, position) }
            .onFailure { showNetworkError(it) }
            .getOrDefault(emptyList())
    }

    private suspend fun fetchProfileCategories(
        positionBlock: Int,
        position: Int,
        userId: Long,
    ): List<PositionItem> {
        return runCatching {
            repository.fetchProfileCategories(
                position,
                positionBlock,
                userId,
                application.isTablet()
            )
        }
            .onFailure { showNetworkError(it) }
            .getOrDefault(emptyList())
    }

    private suspend fun fetchViewedProductsSlider(
        positionTitle: Int,
        position: Int,
        userId: Long,
    ): List<PositionItem> {
        return runCatching { repository.fetchViewedProductsSlider(userId, positionTitle, position) }
            .onFailure { showNetworkError(it) }
            .getOrDefault(emptyList())
    }

    private suspend fun fetchPersonalProducts(position: Int, userId: Long): List<PositionItem> {
        return runCatching { repository.fetchPersonalProducts(position, userId, 1) }
            .onFailure { showNetworkError(it) }
            .getOrDefault(emptyList())
    }

    private fun showNetworkError(throwable: Throwable) {
        val error = throwable.toErrorState()
        if (error is ErrorState.NetworkError) {
            uiStateListener.value = state.copy(error = error)
        }
    }

    fun firstLoad() {
        if (!state.isFirstLoad) {
            uiStateListener.value = state.copy(loadingPage = true)

            val userId = dataRepository.fetchUserId()
            if (userId == null) {
                uiStateListener.value =
                    state.copy(data = state.data.copy(isLogin = false), loadingPage = false)
                return
            }

            viewModelScope.launch {
                val tasks = firstLoadTasks(userId)
                val start = System.currentTimeMillis()
                val result = awaitAll(*tasks).flatten()
                debugLog { "profile first load task ${System.currentTimeMillis() - start} result size ${result.size}" }
                val positionItemsSorted =
                    (state.data.positionItems + result).sortedBy { it.position }
                uiStateListener.value = state.copy(
                    loadingPage = false,
                    data = state.data.copy(
                        positionItems = positionItemsSorted,
                        items = positionItemsSorted.map { it.item },
                        isLogin = true
                    ),
                    isFirstLoad = true,
                    error = if (result.isNotEmpty()) {
                        null
                    } else {
                        state.error
                    }
                )
            }

            secondLoad(userId)
        }
    }

    private fun secondLoad(userId: Long) {
        viewModelScope.launch {
            val tasks = secondLoadTasks(userId)
            val start = System.currentTimeMillis()
            val result = awaitAll(*tasks).flatten()
            val mappedResult = if (result.isNotEmpty()) {
                result + fetchStaticItems()
            } else {
                result
            }
            debugLog { "profile second load task ${System.currentTimeMillis() - start} result size ${mappedResult.size}" }
            val positionItemsSorted =
                (state.data.positionItems + mappedResult).sortedBy { it.position }
            uiStateListener.value = state.copy(
                loadingPage = false,
                data = state.data.copy(
                    positionItems = positionItemsSorted,
                    items = positionItemsSorted.map { it.item },
                    isSecondLoad = true,
                    isLogin = true
                ),
                error = if (mappedResult.isNotEmpty()) {
                    null
                } else {
                    state.error
                }
            )
        }
    }

    fun refresh() {
        if (!state.loadingPage) {
            val userId = dataRepository.fetchUserId()

            if (userId == null) {
                uiStateListener.value = state.copy(data = state.data.copy(isLogin = false))
                return
            }

            uiStateListener.value =
                state.copy(
                    loadingPage = true,
                    data = state.data.copy(
                        items = ProfileState.idle().items,
                        positionItems = ProfileState.idle().positionItems,
                        isSecondLoad = false,
                        isLogin = true
                    ),
                    isFirstLoad = false
                )
            viewModelScope.launch {
                val tasks = firstLoadTasks(userId) + secondLoadTasks(userId)
                val result = awaitAll(*tasks).flatten()
                val mappedResult = if (result.isNotEmpty()) {
                    result + fetchStaticItems()
                } else {
                    result
                }
                val positionItemsSorted =
                    (state.data.positionItems + mappedResult).sortedBy { it.position }
                uiStateListener.value = state.copy(
                    loadingPage = false,
                    data = state.data.copy(
                        positionItems = positionItemsSorted,
                        items = positionItemsSorted.map { it.item },
                        isSecondLoad = true
                    ),
                    error = if (mappedResult.isNotEmpty()) {
                        null
                    } else {
                        state.error
                    },
                    isFirstLoad = true
                )
            }
        }
    }

    fun logout() {
        dataRepository.logout().subscribe()
        viewModelScope.launch {
            tabManager.clearBottomNavProfileState()
            cartManager.clearCart()
            accountManager.removeUserId()
            eventListener.emit(ProfileEvents.Logout)
            localDataSource.removeCookieSessionId()
        }
    }

    fun checkLogin() {
        viewModelScope.launch {
            uiStateListener.value = state.copy(data = state.data.copy(isLogin = isLoginAlready()))
        }
    }

    fun isLoginAlready() = accountManager.isAlreadyLogin()

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
        val positionItems: List<PositionItem>,
        val items: List<Item>,
        val isLogin: Boolean = true,
        val isSecondLoad: Boolean = false,
    ) : State {
        companion object {
            fun idle(): ProfileState {
                return ProfileState(
                    positionItems = emptyList(),
                    items = emptyList()
                )
            }

            fun fetchStaticItems(): List<PositionItem> {
                return listOf(
                    PositionItem(
                        POSITION_5,
                        ProfileLogout()
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
        val item: Item,
    )

    companion object {
        const val POSITION_1 = 1
        const val POSITION_2 = 2
        const val POSITION_3 = 3
        const val POSITION_4 = 4
        const val POSITION_5 = 5
        const val POSITION_6_TITLE = 6
        const val POSITION_7 = 7
        const val POSITION_8 = 8
    }
}