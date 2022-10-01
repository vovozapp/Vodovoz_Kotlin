package com.vodovoz.app.ui.fragment.paginated_products_catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.config.FiltersConfig
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.FilterUI
import com.vodovoz.app.ui.model.FilterValueUI
import com.vodovoz.app.ui.model.custom.FiltersBundleUI
import com.vodovoz.app.util.FilterBuilderExtensions.buildFilterQuery
import com.vodovoz.app.util.FilterBuilderExtensions.buildFilterValueQuery
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.map

class PaginatedProductsCatalogViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val categoryUIMLD = MutableLiveData<CategoryUI>()
    private val filtersAmountMLD = MutableLiveData<Int>()
    private val sortTypeMLD = MutableLiveData<SortType>()
    private val errorMLD = MutableLiveData<String>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val categoryUILD: LiveData<CategoryUI> = categoryUIMLD
    val sortTypeLD: LiveData<SortType> = sortTypeMLD
    val filtersAmountLD: LiveData<Int> = filtersAmountMLD
    val errorLD: LiveData<String> = errorMLD

    lateinit var categoryHeader: CategoryUI

    var filterBundle: FiltersBundleUI = FiltersBundleUI()
    var sortType: SortType = SortType.NO_SORT
    var categoryId: Long = 0

    init {
        updateFiltersAmount()
        sortTypeMLD.value = sortType
    }

    fun updateFilterBundle(filterBundle: FiltersBundleUI) {
        this.filterBundle = filterBundle
        updateFiltersAmount()
        updateCategoryHeader()
    }

    fun updateArgs(categoryId: Long) {
        this.filterBundle = FiltersBundleUI()
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

    fun updateCategoryHeader() {
        dataRepository.fetchCategoryHeader(categoryId)
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
                                categoryUIMLD.value = data
                                viewStateMLD.value = ViewState.Success()
                            }
                        }
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message!!) }
            ).addTo(compositeDisposable)
    }

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