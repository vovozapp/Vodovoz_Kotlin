package com.vodovoz.app.feature.productlistnofilter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment.DataSource
import com.vodovoz.app.ui.model.CategoryUI
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class PaginatedProductsCatalogWithoutFiltersViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val categoryUIMLD = MutableLiveData<CategoryUI>()
    private val sortTypeMLD = MutableLiveData<SortType>()
    private val errorMLD = MutableLiveData<String>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val categoryUILD: LiveData<CategoryUI> = categoryUIMLD
    val sortTypeLD: LiveData<SortType> = sortTypeMLD
    val errorLD: LiveData<String> = errorMLD

    lateinit var dataSource: DataSource
    var categoryHeader: CategoryUI? = null
        set(value) {
            field = value
            value?.let { categoryUIMLD.value = it }
        }

    private val defaultCategory = CategoryUI(
        id = -1,
        name = "Все"
    )

    var categoryUIList = listOf<CategoryUI>()

    var categoryId: Long? = defaultCategory.id
    var sortType: SortType = SortType.NO_SORT

    init { sortTypeMLD.value = sortType }

    fun updateArgs(dataSource: DataSource) {
        this.dataSource = dataSource
        updateCategoryHeader()
    }

    fun updateCategoryId(categoryId: Long?) {
        this.categoryId = categoryId
        updateCategoryHeader()
    }

    fun updateSortType(sortType: SortType) {
        this.sortType = sortType
        updateCategoryHeader()
    }

    fun updateCategoryHeader() = getCategoryHeaderByDataSource(dataSource)
        .subscribeOn(Schedulers.io())
        .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onSuccess = { response ->
                when(response) {
                    is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                    is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                    is ResponseEntity.Success -> {
                        response.data.mapToUI().let { data ->
                            categoryHeader = data
                            checkSelectedFilter(data)
                            viewStateMLD.value = ViewState.Success()
                        }
                    }
                }
            },
            onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message!!) }
        ).addTo(compositeDisposable)

    private fun getCategoryHeaderByDataSource(dataSource: DataSource) = when(dataSource) {
        is DataSource.Brand -> dataRepository.fetchBrandHeader(dataSource.brandId)
        is DataSource.Country -> dataRepository.fetchCountryHeader(dataSource.countryId)
        is DataSource.Discount -> dataRepository.fetchDiscountHeader()
        is DataSource.Novelties -> dataRepository.fetchNoveltiesHeader()
        is DataSource.Slider -> dataRepository.fetchSliderHeader(categoryId = dataSource.categoryId)
    }

    private fun checkSelectedFilter(categoryUI: CategoryUI) {
        if (categoryUI.categoryUIList.isNotEmpty()) {
            categoryUI.categoryUIList = categoryUI.categoryUIList.toMutableList().apply {
                add(0, defaultCategory)
            }
        }
        categoryUIList = categoryUI.categoryUIList
    }

    fun updateProducts() = getProductsByDataSource(dataSource)
        .map { pagingData ->
            pagingData.map { product -> product.mapToUI() }
        }.cachedIn(viewModelScope)

    private fun getProductsByDataSource(dataSource: DataSource) = when(dataSource) {
        is DataSource.Brand -> dataRepository.fetchProductsByBrand(
            brandId = dataSource.brandId,
            code = null,
            categoryId = when(categoryId) {
                defaultCategory.id -> null
                else -> categoryId
            },
            sort = sortType.value,
            orientation = sortType.orientation,
        )
        is DataSource.Country -> dataRepository.fetchProductsByCountry(
            countryId = dataSource.countryId,
            categoryId = when(categoryId) {
                defaultCategory.id -> null
                else -> categoryId
            },
            orientation = sortType.orientation,
            sort = sortType.value,
        )
        is DataSource.Discount -> dataRepository.fetchProductsDiscount(
            categoryId = when(categoryId) {
                defaultCategory.id -> null
                else -> categoryId
            },
            orientation = sortType.orientation,
            sort = sortType.value,
        )
        is DataSource.Novelties -> dataRepository.fetchProductsNovelties(
            categoryId = when(categoryId) {
                defaultCategory.id -> null
                else -> categoryId
            },
            orientation = sortType.orientation,
            sort = sortType.value,
        )
        is DataSource.Slider -> dataRepository.fetchProductsBySlider(
            categoryId = dataSource.categoryId,
            orientation = sortType.orientation,
            sort = sortType.value,
        )
    }

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

}