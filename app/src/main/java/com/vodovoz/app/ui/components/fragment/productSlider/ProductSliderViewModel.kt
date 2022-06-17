package com.vodovoz.app.ui.components.fragment.productSlider

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy

class ProductSliderViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val sliderCategoryUIListMLD = MutableLiveData<List<CategoryDetailUI>>()
    private val stateHideMLD = MutableLiveData<Boolean>()

    val sliderCategoryUIListLD: LiveData<List<CategoryDetailUI>> = sliderCategoryUIListMLD
    val sateHideLD: LiveData<Boolean> = stateHideMLD

    private val compositeDisposable = CompositeDisposable()

    fun getDataByDataSource(dataSource: ProductSliderFragment.DataSource) {
        when(dataSource) {
            is ProductSliderFragment.DataSource.Request -> updateCategorySlider(dataSource.sliderType)
            is ProductSliderFragment.DataSource.Args -> sliderCategoryUIListMLD.value = dataSource.categoryDetailUIList
        }
    }

    fun updateCategorySlider(sliderType: String) {
        Log.i(LogSettings.DEVELOP_LOG, "$sliderType CALL")
        dataRepository.getSliderSubjectByType(sliderType)
            .subscribeBy(
                onNext = { response ->
                    //Log.i(LogSettings.DEVELOP_LOG, "$sliderType ${response.data!!.size}")
                    when(response) {
                        is ResponseEntity.Success -> {
                            response.data?.let { noNullData ->
                                sliderCategoryUIListMLD.value = noNullData.mapToUI()
                            }
                        }
                        is ResponseEntity.Error -> stateHideMLD.value = true
                    }
                },
                onError = { throwable -> Log.i(LogSettings.ID_LOG, throwable.message!!)
                    Log.i(LogSettings.DEVELOP_LOG, "$sliderType ERROR")}
            )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}