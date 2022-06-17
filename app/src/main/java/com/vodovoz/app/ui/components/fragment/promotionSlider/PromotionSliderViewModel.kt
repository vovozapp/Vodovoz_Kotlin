package com.vodovoz.app.ui.components.fragment.promotionSlider

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.mapper.PromotionMapper.mapToUI
import com.vodovoz.app.ui.model.PromotionUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy

class PromotionSliderViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val promotionListMLD = MutableLiveData<List<PromotionUI>>()
    private val stateHideMLD = MutableLiveData<Boolean>()

    val promotionListLD: LiveData<List<PromotionUI>> = promotionListMLD
    val sateHideLD: LiveData<Boolean> = stateHideMLD

    private val compositeDisposable = CompositeDisposable()

    fun getDataByDataSource(dataSource: PromotionSliderFragment.DataSource) {
        when(dataSource) {
            is PromotionSliderFragment.DataSource.Request -> updatePromotionList()
            is PromotionSliderFragment.DataSource.Args -> promotionListMLD.value = dataSource.promotionUIList
        }
    }

    fun updatePromotionList() = dataRepository.promotionSubject.subscribeBy { response ->
        when(response) {
            is ResponseEntity.Success -> {
                response.data?.let { data ->
                    promotionListMLD.value = data.mapToUI()
                }
            }
            is ResponseEntity.Error -> stateHideMLD.value = true
        }
    }.addTo(compositeDisposable)

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}