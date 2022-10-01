package com.vodovoz.app.ui.fragment.past_purchases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.mapper.PastPurchasesHeaderBundleMapper.mapToUI
import com.vodovoz.app.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.model.CategoryUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.map

class PastPurchasesViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val favoriteCategoryUIMLD = MutableLiveData<CategoryUI>()
    private val sortTypeMLD = MutableLiveData<SortType>()
    private val availableTitleMLD = MutableLiveData<String>()
    private val notAvailableTitleMLD = MutableLiveData<String>()
    private val isShowAvailableSettingsMLD = MutableLiveData<Boolean>()
    private val errorMLD = MutableLiveData<String>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val favoriteCategoryUILD: LiveData<CategoryUI> = favoriteCategoryUIMLD
    val sortTypeLD: LiveData<SortType> = sortTypeMLD
    val availableTitleLD: LiveData<String> = availableTitleMLD
    val notAvailableTitleLD: LiveData<String> = notAvailableTitleMLD
    val isShowAvailableSettingsLD: LiveData<Boolean> = isShowAvailableSettingsMLD
    val errorLD: LiveData<String> = errorMLD

    private val compositeDisposable = CompositeDisposable()

    private var categoryHeader: CategoryUI? = null
        set(value) {
            field = value
            value?.let { favoriteCategoryUIMLD.value = it }
        }

    private val defaultCategory = CategoryUI(
        id = -1,
        name = "Все"
    )

    var categoryUIList = listOf<CategoryUI>()

    var categoryId: Long? = defaultCategory.id
    var sortType: SortType = SortType.NO_SORT
    var isAvailable: Boolean = true

    init { sortTypeMLD.value = sortType }

    fun updateCategoryId(categoryId: Long?) {
        this.categoryId = categoryId
        updateFavoriteProductsHeader()
    }

    fun updateSortType(sortType: SortType) {
        this.sortType = sortType
        sortTypeMLD.value = sortType
        updateFavoriteProductsHeader()
    }

    fun updateIsAvailable(isAvailable: Boolean) {
        this.isAvailable = isAvailable
        updateFavoriteProductsHeader()
    }

    fun updateFavoriteProductsHeader() {
        dataRepository
            .fetchPastPurchasesHeader()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                     when(response) {
                         is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Error("Hide")
                         is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                         is ResponseEntity.Success -> {
                             val data = response.data.mapToUI()
                             data.favoriteCategoryUI?.let { categoryUI ->
                                 checkSelectedFilter(categoryUI)
                                 favoriteCategoryUIMLD.value = categoryUI
                             }
                             var isShowAvailableContainer = false
                             data.availableTitle?.let {
                                 availableTitleMLD.value = it
                                 isShowAvailableContainer = true
                             }
                             data.notAvailableTitle?.let {
                                 notAvailableTitleMLD.value = it
                                 isShowAvailableContainer = true
                             }
                             isShowAvailableSettingsMLD.value = isShowAvailableContainer
                             viewStateMLD.value = ViewState.Success()
                         }
                     }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message ?: "Неизвестная ошибка") }
            ).addTo(compositeDisposable)
    }

    private fun checkSelectedFilter(categoryUI: CategoryUI) {
        if (categoryUI.categoryUIList.isNotEmpty()) {
            categoryUI.categoryUIList = categoryUI.categoryUIList.toMutableList().apply {
                add(0, defaultCategory)
            }
        }
        categoryUIList = categoryUI.categoryUIList
    }

    fun updateFavoriteProducts() = dataRepository
        .fetchPastPurchasesProducts(
            categoryId = when(categoryId) {
                 -1L -> null
                else -> categoryId
            },
            sort = sortType.value,
            orientation = sortType.orientation,
            isAvailable = isAvailable
        )
        .map { pagingData ->
            pagingData.map { product -> product.mapToUI() }
        }.cachedIn(viewModelScope)

    fun changeFavoriteStatus(productId: Long, isFavorite: Boolean) {
        when(isFavorite) {
            true -> dataRepository.addToFavorite(productId)
            false -> dataRepository.removeFromFavorite(productId = productId)

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {},
                onError = { throwable -> errorMLD.value = throwable.message ?: "Неизвестная ошибка" }
            ).addTo(compositeDisposable)
    }

    fun changeCart(productId: Long, quantity: Int) {
        dataRepository
            .changeCart(productId, quantity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = { errorMLD.value = it.message ?: "Неизвестная ошибка" })
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun isAlreadyLogin() = dataRepository.isAlreadyLogin()

}