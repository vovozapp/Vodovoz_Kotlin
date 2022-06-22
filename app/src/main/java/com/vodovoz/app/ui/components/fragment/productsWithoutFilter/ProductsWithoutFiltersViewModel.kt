package com.vodovoz.app.ui.components.fragment.productsWithoutFilter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.config.FiltersConfig
import com.vodovoz.app.data.model.common.FilterValueEntity
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.components.fragment.productsWithoutFilter.ProductsWithoutFiltersFragment.DataSource
import com.vodovoz.app.ui.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.ui.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.FilterUI
import com.vodovoz.app.ui.model.FilterValueUI
import com.vodovoz.app.ui.model.custom.FilterBundleUI
import com.vodovoz.app.util.FilterBuilderExtensions.buildFilterQuery
import com.vodovoz.app.util.FilterBuilderExtensions.buildFilterValueQuery
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.*

class ProductsWithoutFiltersViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {


    private val compositeDisposable = CompositeDisposable()

    private val fetchStateMLD = MutableLiveData<FetchState<CategoryUI>>()
    private val sortTypeMLD = MutableLiveData<SortType>()

    val fetchStateLD: LiveData<FetchState<CategoryUI>> = fetchStateMLD
    val sortTypeLD: LiveData<SortType> = sortTypeMLD

    lateinit var categoryHeader: CategoryUI
    lateinit var dataSource: DataSource

    val defaultCategory = CategoryUI(
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
        .doOnSubscribe { fetchStateMLD.value = FetchState.Loading() }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onSuccess = { response ->
                response.data?.let {
                    categoryHeader = response.data.mapToUI()
                    checkSelectedFilter(categoryHeader)
                    fetchStateMLD.value = FetchState.Success(categoryHeader)
                }
            },
            onError = { throwable -> fetchStateMLD.value = FetchState.Error(throwable.message!!) }
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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}