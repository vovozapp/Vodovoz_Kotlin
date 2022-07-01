package com.vodovoz.app.ui.components.fragment.all_promotions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.mapper.AllPromotionBundleMapper.mapToUI
import com.vodovoz.app.ui.model.PromotionFilterUI
import com.vodovoz.app.ui.model.custom.AllPromotionBundleUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class AllPromotionsViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val selectedFilterMLD = MutableLiveData<PromotionFilterUI>()
    private val promotionBundleUIMLD = MutableLiveData<AllPromotionBundleUI>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val selectedFilterLD: LiveData<PromotionFilterUI> = selectedFilterMLD
    val promotionBundleUILD: LiveData<AllPromotionBundleUI> = promotionBundleUIMLD

    private val compositeDisposable = CompositeDisposable()

    private val defaultFilter = PromotionFilterUI(
        id = 0,
        name = "Все акции",
        code = ""
    )

    var selectedFilter = defaultFilter
        set(value) {
            field = value
            selectedFilterMLD.value = value
        }

    var promotionFilterUIList = mutableListOf(defaultFilter)

    init { selectedFilterMLD.value = defaultFilter }

    private lateinit var dataSource: AllPromotionsFragment.DataSource

    fun updateArgs(dataSource: AllPromotionsFragment.DataSource) {
        this.dataSource = dataSource
        updateData()
    }

    fun updateSelectedFilter(filterId: Long) {
        promotionFilterUIList.find { it.id == filterId }?.let { noNullFilter ->
            selectedFilter = noNullFilter
            updateData()
        }
    }

    fun updateData() {
        getPromotionsByDataSource(dataSource)
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
                                promotionBundleUIMLD.value = data
                                promotionFilterUIList = mutableListOf(defaultFilter).apply {
                                    addAll(data.promotionFilterUIList.toMutableList())
                                }
                                viewStateMLD.value = ViewState.Success()
                            }
                        }
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message!!) }
            ).addTo(compositeDisposable)
    }

    private fun getPromotionsByDataSource(dataSource: AllPromotionsFragment.DataSource) = when(dataSource) {
        is AllPromotionsFragment.DataSource.All -> dataRepository.fetchAllPromotions(filterId = selectedFilter.id)
        is AllPromotionsFragment.DataSource.ByBanner -> dataRepository.fetchPromotionsByBanner(categoryId = dataSource.categoryId)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}