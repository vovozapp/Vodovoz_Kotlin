package com.vodovoz.app.ui.components.fragment.slider.country_slider

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.mapper.CountryMapper.mapToUI
import com.vodovoz.app.ui.model.CountryUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class CountrySliderViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val countryUIListMLD = MutableLiveData<List<CountryUI>>()
    private val backgroundImageUrlMLD = MutableLiveData<String>()
    private val titleMLD = MutableLiveData<String>()
    private val viewStateMLD = MutableLiveData<ViewState>()

    val countryUIListLD: LiveData<List<CountryUI>> = countryUIListMLD
    val backgroundImageUrlLD: LiveData<String> = backgroundImageUrlMLD
    val titleLD: LiveData<String> = titleMLD
    val viewStateLD: LiveData<ViewState> = viewStateMLD

    private val compositeDisposable = CompositeDisposable()

    fun updateData() {
        dataRepository.fetchCountriesSlider()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    Log.i(LogSettings.REQ_RES_LOG, "READY COUNTRY")
                    when(response) {
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                        is ResponseEntity.Success -> {
                            response.data.let { data ->
                                viewStateMLD.value = ViewState.Success()
                                countryUIListMLD.value = data.countryEntityList.mapToUI()
                                backgroundImageUrlMLD.value = data.backgroundPicture
                                titleMLD.value = data.title
                            }
                        }
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message!!) }
            ).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}