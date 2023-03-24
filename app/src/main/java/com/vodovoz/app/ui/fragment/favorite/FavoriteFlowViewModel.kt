package com.vodovoz.app.ui.fragment.favorite

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.favorite.FavoriteHeaderResponseJsonParser.parseFavoriteProductsHeaderBundleResponse
import com.vodovoz.app.mapper.FavoriteProductsHeaderBundleMapper.mapToUI
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val localDataSource: LocalDataSource,
    private val dataRepository: DataRepository,
    private val cartManager: CartManager,
    private val likeManager: LikeManager
) : PagingStateViewModel<FavoriteFlowViewModel.FavoriteState>(FavoriteState()){

    fun firstLoad() {
        if (!state.isFirstLoad) {
            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            fetchFavoriteProductsHeader()
        }
    }

    fun refresh() {
        uiStateListener.value = state.copy(loadingPage = true)
        fetchFavoriteProductsHeader()
    }

    private fun fetchFavoriteProductsHeader() {
        val userId = localDataSource.fetchUserId()

        viewModelScope.launch {
            flow { emit(repository.fetchFavoriteProducts(userId = userId, null)) }
                .catch {
                    debugLog { "fetch favorite products error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseFavoriteProductsHeaderBundleResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                favoriteCategory = checkSelectedFilter(data.favoriteCategoryUI),
                                bestForYouCategoryDetailUI = data.bestForYouCategoryDetailUI,
                                availableTitle = data.availableTitle,
                                notAvailableTitle = data.notAvailableTitle
                            )
                        )
                    } else {
                        uiStateListener.value = state.copy(loadingPage = false, error = ErrorState.Error())
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun checkSelectedFilter(categoryUI: CategoryUI?): CategoryUI? {
        if (categoryUI == null) return null

        if (categoryUI.categoryUIList.isNotEmpty()) {
            categoryUI.categoryUIList = categoryUI.categoryUIList.toMutableList().apply {
                add(
                    0, CategoryUI(
                        id = -1,
                        name = "Все",
                        isSelected = true
                    )
                )
            }
        }
        return categoryUI
    }

    fun onTabClick(id: Long) {
        val categoryUI = state.data.favoriteCategory ?: return

        uiStateListener.value = state.copy(
            data = state.data.copy(
                favoriteCategory = categoryUI.copy(
                    categoryUIList = categoryUI.categoryUIList.map { it.copy(isSelected = it.id == id) }
                )
            )
        )
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

    data class FavoriteState(
        val favoriteCategory: CategoryUI? = null,
        val bestForYouCategoryDetailUI: CategoryDetailUI? = null,
        val availableTitle: String? = null,
        val notAvailableTitle: String? = null
    ) : State
}