package com.vodovoz.app.ui.components.fragment.countrySlider

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.mapper.CountryMapper.mapToUI
import com.vodovoz.app.ui.model.CountryUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy

class CountrySliderViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val countryUIListMLD = MutableLiveData<List<CountryUI>>()
    private val backgroundImageUrlMLD = MutableLiveData<String>()
    private val titleMLD = MutableLiveData<String>()
    private val stateHideMLD = MutableLiveData<Boolean>()

    val countryUIListLD: LiveData<List<CountryUI>> = countryUIListMLD
    val backgroundImageUrlLD: LiveData<String> = backgroundImageUrlMLD
    val titleLD: LiveData<String> = titleMLD
    val sateHideLD: LiveData<Boolean> = stateHideMLD

    private val compositeDisposable = CompositeDisposable()

    init {
        dataRepository.countrySubject
            .subscribeBy { response ->
                when(response) {
                    is ResponseEntity.Success -> {
                        response.data?.let { data ->
                            countryUIListMLD.value = data.countryEntityList.mapToUI()
                            backgroundImageUrlMLD.value = data.backgroundPicture
                            titleMLD.value = data.title
                        }
                    }
                    is ResponseEntity.Error -> {
                        stateHideMLD.value = true
                    }
                }
            }
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}