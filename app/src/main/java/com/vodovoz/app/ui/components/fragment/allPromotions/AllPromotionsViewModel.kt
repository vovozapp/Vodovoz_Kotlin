package com.vodovoz.app.ui.components.fragment.allPromotions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.mapper.AllPromotionBundleMapper.mapToUI
import com.vodovoz.app.ui.model.PromotionFilterUI
import com.vodovoz.app.ui.model.custom.AllPromotionBundleUI
import com.vodovoz.app.ui.model.custom.FilterBundleUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class AllPromotionsViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val fetchStateMLD = MutableLiveData<FetchState<AllPromotionBundleUI>>()
    private val selectedFilterMLD = MutableLiveData<PromotionFilterUI>()

    val fetchStateLD: LiveData<FetchState<AllPromotionBundleUI>> = fetchStateMLD
    val selectedFilterLD: LiveData<PromotionFilterUI> = selectedFilterMLD

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

    init {
        selectedFilterMLD.value = defaultFilter
        updateData()
    }

    fun updateSelectedFilter(filterId: Long) {
        promotionFilterUIList.find { it.id == filterId }?.let { noNullFilter ->
            selectedFilter = noNullFilter
            updateData()
        }
    }

    fun updateData() {
        dataRepository
            .fetchAllPromotions(filterId = selectedFilter.id)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { fetchStateMLD.value = FetchState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    val data = response.data?.mapToUI()
                    promotionFilterUIList = mutableListOf(defaultFilter).apply {
                        addAll(data!!.promotionFilterUIList.toMutableList())
                    }
                    fetchStateMLD.value = FetchState.Success(data)
                },
                onError = { throwable -> fetchStateMLD.value = FetchState.Error(throwable.message) }
            )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}