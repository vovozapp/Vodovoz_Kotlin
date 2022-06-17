package com.vodovoz.app.ui.components.fragment.bannerSlider

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.mapper.BannerMapper.mapToUI
import com.vodovoz.app.ui.model.BannerUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class BannerSliderViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

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

    fun setBannerType(bannerType: String) {
        dataRepository.getBannerSubjectByType(bannerType)
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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        changeBannerTimer.cancel()
    }

}