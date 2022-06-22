package com.vodovoz.app.ui.components.fragment.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.config.FiltersConfig
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.ui.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.FilterUI
import com.vodovoz.app.ui.model.FilterValueUI
import com.vodovoz.app.ui.model.custom.FilterBundleUI
import com.vodovoz.app.util.FilterBuilderExtensions.buildFilterQuery
import com.vodovoz.app.util.FilterBuilderExtensions.buildFilterValueQuery
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.*

class ProductsViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val fetchStateMLD = MutableLiveData<FetchState<CategoryUI>>()
    private val filtersAmountMLD = MutableLiveData<Int>()
    private val sortTypeMLD = MutableLiveData<SortType>()

    val fetchStateLD: LiveData<FetchState<CategoryUI>> = fetchStateMLD
    val sortTypeLD: LiveData<SortType> = sortTypeMLD
    val filtersAmountLD: LiveData<Int> = filtersAmountMLD

    lateinit var categoryHeader: CategoryUI

    var filterBundle: FilterBundleUI = FilterBundleUI()
    var sortType: SortType = SortType.NO_SORT
    var categoryId: Long = 0

    init {
        updateFiltersAmount()
        sortTypeMLD.value = sortType
    }

    fun updateFilterBundle(filterBundle: FilterBundleUI) {
        this.filterBundle = filterBundle
        updateFiltersAmount()
        updateCategoryHeader()
    }

    fun updateArgs(categoryId: Long) {
        this.filterBundle = FilterBundleUI()
        this.categoryId = categoryId
        updateCategoryHeader()
    }

    fun updateSortType(sortType: SortType) {
        this.sortType = sortType
        updateCategoryHeader()
    }

    fun addPrimaryFilterValue(filterValue: FilterValueUI) {
        filterBundle.filterUIList.removeAll { it.code == FiltersConfig.BRAND_FILTER_CODE }
        filterBundle.filterUIList.add(FilterUI(
                code = FiltersConfig.BRAND_FILTER_CODE,
                name = FiltersConfig.BRAND_FILTER_NAME,
                filterValueList = mutableListOf(filterValue)
        ))
        updateFiltersAmount()
        updateCategoryHeader()
    }

    private fun updateFiltersAmount() {
        var filtersAmount = 0
        if (filterBundle.filterPriceUI.minPrice != Int.MIN_VALUE
            || filterBundle.filterPriceUI.maxPrice != Int.MAX_VALUE) {
            filtersAmount++
        }

        filtersAmount += filterBundle.filterUIList.size

        filtersAmountMLD.value = filtersAmount
    }

    fun updateCategoryHeader() = dataRepository
        .fetchCategoryHeader(categoryId)
        .subscribeOn(Schedulers.io())
        .doOnSubscribe { fetchStateMLD.value = FetchState.Loading() }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onSuccess = { response ->
                response.data?.let {
                    categoryHeader = response.data.mapToUI()
                    fetchStateMLD.value = FetchState.Success(categoryHeader)
                }
            },
            onError = { throwable -> fetchStateMLD.value = FetchState.Error(throwable.message!!) }
        ).addTo(compositeDisposable)

    fun updateProductList() = dataRepository
        .fetchProductsByCategory(
            categoryId = categoryId,
            sort = sortType.value,
            orientation = sortType.orientation,
            filter = filterBundle.filterUIList.buildFilterQuery(),
            filterValue = filterBundle.filterUIList.buildFilterValueQuery(),
            priceFrom = filterBundle.filterPriceUI.minPrice,
            priceTo = filterBundle.filterPriceUI.maxPrice
        ).map { pagingData ->
            pagingData.map { product -> product.mapToUI() }
        }.cachedIn(viewModelScope)

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}