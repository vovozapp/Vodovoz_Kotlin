package com.vodovoz.app.ui.components.fragment.slider.banner_slider

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.mapper.BannerMapper.mapToUI
import com.vodovoz.app.ui.model.BannerUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class BannerSliderViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    companion object {
        const val ADVERTISING_BANNERS_SLIDER = "advertising_slider"
        const val CATEGORY_BANNERS_SLIDER = "category_banners_slider"
    }

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val bannerUIListMLD = MutableLiveData<List<BannerUI>>()
    private val currentBannerIndexMLD = MutableLiveData<Int>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val bannerUIListLD: LiveData<List<BannerUI>> = bannerUIListMLD
    val currentBannerIndexLD: LiveData<Int> = currentBannerIndexMLD

    private var currentBannerIndex = 0
    private var bannerLength = 0
    private var changeBannerTimer: CountDownTimer? = null

    private val compositeDisposable = CompositeDisposable()

    private lateinit var sliderType: String

    fun updateArgs(sliderType: String) {
        this.sliderType = sliderType
        updateData()
    }

    fun updateData() {
        getBannersSliderByType()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    Log.i(LogSettings.REQ_RES_LOG, "READY BANNER")
                    when(response) {
                        is ResponseEntity.Success -> {
                            response.data.mapToUI().let { data ->
                                viewStateMLD.value = ViewState.Success()
                                bannerLength = data.size
                                bannerUIListMLD.value = data
                                //initCountDownTimer()
                            }
                        }
                        is ResponseEntity.Error -> { viewStateMLD.value = ViewState.Error(response.errorMessage) }
                        is ResponseEntity.Hide -> { viewStateMLD.value = ViewState.Hide() }
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message!!) }
            ).addTo(compositeDisposable)
    }

    private fun getBannersSliderByType() = when(sliderType) {
        ADVERTISING_BANNERS_SLIDER-> dataRepository.fetchAdvertisingBannersSlider()
        CATEGORY_BANNERS_SLIDER -> dataRepository.fetchCategoryBannersSlider()
        else -> throw Exception()
    }

    private fun initCountDownTimer() {
        changeBannerTimer = object: CountDownTimer(3000, 3000) {
            override fun onTick(millisUntilFinished: Long) {

            }
            override fun onFinish() {
                currentBannerIndex = (currentBannerIndex + 1) % bannerLength
                currentBannerIndexMLD.value = currentBannerIndex
                start()
            }
        }
        changeBannerTimer?.start()
    }

    fun restartCountDownTimer(position: Int) {
        this.currentBannerIndex = position
        changeBannerTimer?.cancel()
        changeBannerTimer?.start()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        changeBannerTimer?.cancel()
    }

}