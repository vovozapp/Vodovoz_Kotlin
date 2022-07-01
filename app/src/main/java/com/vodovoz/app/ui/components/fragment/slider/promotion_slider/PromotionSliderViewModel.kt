package com.vodovoz.app.ui.components.fragment.slider.promotion_slider

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.mapper.PromotionMapper.mapToUI
import com.vodovoz.app.ui.model.PromotionUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class PromotionSliderViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val promotionListMLD = MutableLiveData<List<PromotionUI>>()
    private val viewStateMLD = MutableLiveData<ViewState>()

    val promotionListLD: LiveData<List<PromotionUI>> = promotionListMLD
    val viewStateLD: LiveData<ViewState> = viewStateMLD

    private val compositeDisposable = CompositeDisposable()

    private lateinit var dataSource: PromotionSliderFragment.DataSource

    fun updateArgs(dataSource: PromotionSliderFragment.DataSource) {
        this.dataSource = dataSource
        updateData()
    }

    fun updateData() {
        when(dataSource) {
            is PromotionSliderFragment.DataSource.Request -> {
                dataRepository.fetchPromotionsSlider()
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy { response ->
                        Log.i(LogSettings.REQ_RES_LOG, "READY PROMOTION")
                        when(response) {
                            is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                            is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                            is ResponseEntity.Success -> {
                                viewStateMLD.value = ViewState.Success()
                                promotionListMLD.value = response.data.mapToUI()
                            }
                        }
                    }.addTo(compositeDisposable)
            }
            is PromotionSliderFragment.DataSource.Args -> promotionListMLD.value = (dataSource as PromotionSliderFragment.DataSource.Args).promotionUIList
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}