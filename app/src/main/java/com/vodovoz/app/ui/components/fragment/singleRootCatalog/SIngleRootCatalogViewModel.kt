package com.vodovoz.app.ui.components.fragment.singleRootCatalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.custom.MiniCatalogBundleUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class SIngleRootCatalogViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val fetchStateMLD = MutableLiveData<FetchState<MiniCatalogBundleUI>>()
    private val wayMLD = MutableLiveData<List<CategoryUI>>()

    val fetchStateLD: LiveData<FetchState<MiniCatalogBundleUI>> = fetchStateMLD
    val wayLD: LiveData<List<CategoryUI>> = wayMLD

    private val compositeDisposable = CompositeDisposable()

    var rootCategory: CategoryUI? = null
    var selectedCategoryId: Long? = null

    fun fetchCategory() {
        dataRepository.fetchCatalog()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { fetchStateMLD.value = FetchState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    response.data?.let {
                        rootCategory = getRoot(response.data.mapToUI())
                        fetchStateMLD.value = FetchState.Success(buildBundle())
                    }
                },
                onError = { throwable -> fetchStateMLD.value = FetchState.Error(throwable.message!!)}
            ).addTo(compositeDisposable)
    }

    private fun buildBundle() = MiniCatalogBundleUI(
        way = findWay(currentPoint = rootCategory!!, endPointId = selectedCategoryId!!)!!,
        categoryUIList = listOf(rootCategory!!)
    )

    fun changeSelectedCategory(category: CategoryUI) {
        selectedCategoryId = category.id
        fetchStateMLD.value = FetchState.Success(buildBundle())
    }

    private fun getRoot(categoryList: List<CategoryUI>): CategoryUI? {
        for (category in categoryList) {
            if (category.id == selectedCategoryId) return category
            if (findRoot(category.categoryUIList)) return category
        }
        return null
    }

    private fun findRoot(categoryList: List<CategoryUI>): Boolean  {
        for (category in categoryList) {
            if (category.id == selectedCategoryId) return true
            if (findRoot(category.categoryUIList)) return true
        }
        return false
    }

    private fun findWay(
        way: MutableList<CategoryUI> = mutableListOf(),
        currentPoint: CategoryUI,
        endPointId: Long
    ): List<CategoryUI>? {
        if (currentPoint.id == endPointId) {
            way.add(currentPoint)
            return way
        }

        if (currentPoint.categoryUIList.isEmpty()) return null

        for (category in currentPoint.categoryUIList) {
            val childWay = findWay(
                mutableListOf<CategoryUI>().apply {
                    addAll(way)
                    add(currentPoint)
                }, category, endPointId)

            when(childWay) {
                null -> continue
                else -> return childWay
            }
        }

        return null
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}