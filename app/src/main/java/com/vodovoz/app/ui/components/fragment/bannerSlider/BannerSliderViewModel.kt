package com.vodovoz.app.ui.components.fragment.bannerSlider

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.fragment.productSlider.ProductSliderViewModel
import com.vodovoz.app.ui.mapper.BannerMapper.mapToUI
import com.vodovoz.app.ui.model.BannerUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class BannerSliderViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    companion object {
        const val ADVERTISING_BANNERS_SLIDER = "advertising_slider"
        const val CATEGORY_BANNERS_SLIDER = "category_banners_slider"
    }

    private val mainBannerListMLD = MutableLiveData<List<BannerUI>>()
    private val currentBannerIndexMLD = MutableLiveData<Int>()
    private val stateHideMLD = MutableLiveData<Boolean>()

    val mainBannerListLD: LiveData<List<BannerUI>> = mainBannerListMLD
    val currentBannerIndexLD: LiveData<Int> = currentBannerIndexMLD
    val sateHideLD: LiveData<Boolean> = stateHideMLD

    private var currentBannerIndex = 0
    private var bannerLength = 0

    private lateinit var changeBannerTimer: CountDownTimer

    private val compositeDisposable = CompositeDisposable()

    fun setBannerType(sliderType: String) {
        getBannersSliderByType(sliderType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { response ->
                when(response) {
                    is ResponseEntity.Success -> {
                        response.data?.let {
                            mainBannerListMLD.value = response.data.mapToUI()
                            bannerLength = response.data.size
                            initCountDownTimer()
                        }
                    }
                    is ResponseEntity.Error-> {
                        stateHideMLD.value = true
                    }
                }
            }
    }

    private fun getBannersSliderByType(sliderType: String) = when(sliderType) {
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
        changeBannerTimer.start()
    }

    fun restartCountDownTimer(position: Int) {
        this.currentBannerIndex = position
        Log.i(LogSettings.ID_LOG, "POSITION = $position")
        changeBannerTimer.cancel()
        changeBannerTimer.start()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        changeBannerTimer.cancel()
    }

}