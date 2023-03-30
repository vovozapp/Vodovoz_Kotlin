package com.vodovoz.app.feature.search.old

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
import com.vodovoz.app.mapper.DefaultSearchDataBundleMapper.mapToUI
import com.vodovoz.app.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.CategoryUI
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val headerCategoryUIMLD = MutableLiveData<CategoryUI>()
    private val popularCategoryDetailUIMLD = MutableLiveData<CategoryDetailUI>()
    private val popularQueryListMLD = MutableLiveData<List<String>>()
    private val historyQueryListMLD = MutableLiveData<List<String>>()
    private val matchesQueryListMLD = MutableLiveData<List<String>>()
    private val sortTypeMLD = MutableLiveData<SortType>()
    private val errorMLD = MutableLiveData<String>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val headerCategoryUILD: LiveData<CategoryUI> = headerCategoryUIMLD
    val popularCategoryDetailUILD: LiveData<CategoryDetailUI> = popularCategoryDetailUIMLD
    val popularQueryListLD: LiveData<List<String>> = popularQueryListMLD
    val historyQueryListLD: LiveData<List<String>> = historyQueryListMLD
    val matchesQueryListLD: LiveData<List<String>> = matchesQueryListMLD
    val sortTypeLD: LiveData<SortType> = sortTypeMLD
    val errorLD: LiveData<String> = errorMLD

    private var matchesQueriesDisposable: Disposable? = null
    private val compositeDisposable = CompositeDisposable()

    var categoryHeader: CategoryUI? = null
        set(value) {
            field = value
            value?.let { headerCategoryUIMLD.value = it }
        }

    private val defaultCategory = CategoryUI(
        id = -1,
        name = "Все"
    )

    var categoryUIList = listOf<CategoryUI>()

    var categoryId: Long = defaultCategory.id!!
    var sortType: SortType = SortType.NO_SORT
    var query: String = "Сухарики"

    init { sortTypeMLD.value = sortType }

    fun updateQuery(query: String) {
        this.query = query
        updateHeader()
    }

    fun updateCategoryId(categoryId: Long) {
        this.categoryId = categoryId
        updateHeader()
    }

    fun updateSortType(sortType: SortType) {
        this.sortType = sortType
        sortTypeMLD.value = sortType
        updateHeader()
    }

    fun updateDefaultSearchData() {
        historyQueryListMLD.value = dataRepository.fetchSearchHistory()
        dataRepository
            .fetchSearchDefaultData()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Error("Неизвестная ошибка")
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                        is ResponseEntity.Success -> {
                            viewStateMLD.value = ViewState.Success()
                            val data = response.data.mapToUI()
                            popularCategoryDetailUIMLD.value = data.popularProductsCategoryDetailUI
                            popularQueryListMLD.value = data.popularQueryList
                        }
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message ?: "Неизвестная ошибка") }
            ).addTo(compositeDisposable)
    }

    fun updateHeader() {
        if (query.isNotEmpty()) {
            dataRepository.addQueryToHistory(query)
            historyQueryListMLD.value = dataRepository.fetchSearchHistory()
        }

        dataRepository
            .fetchProductsByQueryHeader(query = query)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Error("Hide")
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                        is ResponseEntity.Success -> {
                            categoryHeader = response.data.mapToUI().apply { checkSelectedFilter(this) }
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

    fun updateProductsByQuery() = dataRepository
        .fetchProductsByQuery(
            query = query,
            categoryId = when(categoryId) {
                -1L -> null
                else -> categoryId
            },
            sort = sortType.value,
            orientation = sortType.orientation
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

    fun updateMatchesQueries(query: String?) {
        matchesQueriesDisposable?.dispose()
        matchesQueriesDisposable = dataRepository
            .fetchMatchesQueries(query)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Success -> matchesQueryListMLD.value = response.data
                        else -> matchesQueryListMLD.value = listOf()
                    }
                },
                onError = { matchesQueryListMLD.value = listOf() }
            )
    }

    fun clearSearchHistory() {
        dataRepository.clearSearchHistory()
        historyQueryListMLD.value = listOf()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
        matchesQueriesDisposable?.dispose()
    }

    fun isAlreadyLogin() = dataRepository.isAlreadyLogin()

}