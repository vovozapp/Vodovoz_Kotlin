package com.vodovoz.app.ui.components.fragment.brandSlider

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.mapper.BrandMapper.mapToUI
import com.vodovoz.app.ui.model.BrandUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class BrandSliderViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val brandUIListMLD = MutableLiveData<List<BrandUI>>()
    private val stateHideMLD = MutableLiveData<Boolean>()

    val brandUIListLD: LiveData<List<BrandUI>> = brandUIListMLD
    val sateHideLD: LiveData<Boolean> = stateHideMLD

    private val compositeDisposable = CompositeDisposable()

    init {
        dataRepository.fetchBrandsSlider()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy { response ->
                when(response) {
                    is ResponseEntity.Success -> {
                        response.data?.let { noNullData ->
                            brandUIListMLD.value = noNullData.mapToUI()
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