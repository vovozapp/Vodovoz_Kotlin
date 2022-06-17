package com.vodovoz.app.ui.components.fragment.popularSlider

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.ui.model.CategoryUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy

class PopularSliderViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val popularSectionIUDataListMLD = MutableLiveData<List<CategoryUI>>()
    private val stateHideMLD = MutableLiveData<Boolean>()

    val popularSectionIUDataListLD: LiveData<List<CategoryUI>> = popularSectionIUDataListMLD
    val sateHideLD: LiveData<Boolean> = stateHideMLD

    private val compositeDisposable = CompositeDisposable()

    init {
        dataRepository.popularSubject
            .subscribeBy { response ->
                when(response) {
                    is ResponseEntity.Success -> {
                        response.data?.let { data ->
                            popularSectionIUDataListMLD.value =
                                data.mapToUI()
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