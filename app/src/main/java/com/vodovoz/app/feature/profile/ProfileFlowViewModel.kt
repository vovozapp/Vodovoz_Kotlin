package com.vodovoz.app.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.feature.home.HomeFlowViewModel
import com.vodovoz.app.feature.map.MapFlowViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val localDataSource: LocalDataSource,
    private val dataRepository: DataRepository,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager
) : PagingContractViewModel<ProfileFlowViewModel.ProfileState, ProfileFlowViewModel.ProfileEvents>(
    ProfileState()
) {

    private fun loadPage() {
        fetchProfileData()
        fetchOrdersSlider()
        fetchViewedProductsSlider()
        fetchPersonalProducts()
        fetchProfileCategories()
    }

    private fun fetchProfileData() {

    }

    private fun fetchProfileCategories() {

    }

    private fun fetchOrdersSlider() {

    }

    private fun fetchViewedProductsSlider() {

    }

    private fun fetchPersonalProducts() {

    }

    fun firstLoad() {
        if (!state.isFirstLoad) {
            uiStateListener.value =
                state.copy(isFirstLoad = true, loadingPage = true)
            loadPage()
        }
    }

    fun refresh() {
        uiStateListener.value =
            state.copy(loadingPage = true, bottomItem = null)
        loadPage()
    }

    fun logout() {
        dataRepository.logout().subscribe()
        viewModelScope.launch {
            eventListener.emit(ProfileEvents.Logout)
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
        val items: List<Item> = emptyList()
    ): State

    sealed class ProfileEvents: Event {
        object Logout: ProfileEvents()
    }
}