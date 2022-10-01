package com.vodovoz.app.ui.fragment.single_root_catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.custom.SingleRootCatalogBundleUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class SingleRootCatalogViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val wayMLD = MutableLiveData<List<CategoryUI>>()
    private val singleRootCatalogBundleUIMLD = MutableLiveData<SingleRootCatalogBundleUI>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val wayLD: LiveData<List<CategoryUI>> = wayMLD
    val singleRootCatalogBundleUILD: LiveData<SingleRootCatalogBundleUI> = singleRootCatalogBundleUIMLD

    private val compositeDisposable = CompositeDisposable()

    var rootCategory: CategoryUI? = null
    var selectedCategoryId: Long? = null

    fun fetchCategory() {
        dataRepository.fetchCatalog()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                        is ResponseEntity.Success -> {
                            rootCategory = getRoot(response.data.mapToUI())
                            singleRootCatalogBundleUIMLD.value = buildBundle()
                            viewStateMLD.value = ViewState.Success()
                        }
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message!!)}
            ).addTo(compositeDisposable)
    }

    private fun buildBundle() = SingleRootCatalogBundleUI(
        way = findWay(currentPoint = rootCategory!!, endPointId = selectedCategoryId!!)!!,
        categoryUIList = listOf(rootCategory!!)
    )

    fun changeSelectedCategory(category: CategoryUI) {
        selectedCategoryId = category.id
        singleRootCatalogBundleUIMLD.value = buildBundle()
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
        compositeDisposable.dispose()
    }

}