package com.vodovoz.app.ui.components.fragment.productSlider

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.CategoryEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class ProductSliderViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    companion object {
        const val DISCOUNT_PRODUCTS_SLIDER = "discount_products_slider"
        const val NOVELTIES_PRODUCTS_SLIDER = "novelties_products_slider"
        const val TOP_PRODUCTS_SLIDER = "top_products_slider"
        const val BOTTOM_PRODUCTS_SLIDER = "bottom_products_slider"
        const val VIEWED_PRODUCTS_SLIDER = "viewed_products_slider"
    }

    private val sliderCategoryUIListMLD = MutableLiveData<List<CategoryDetailUI>>()
    private val stateHideMLD = MutableLiveData<Boolean>()

    val sliderCategoryUIListLD: LiveData<List<CategoryDetailUI>> = sliderCategoryUIListMLD
    val sateHideLD: LiveData<Boolean> = stateHideMLD

    private val compositeDisposable = CompositeDisposable()

    lateinit var categoryDetailUIList: List<CategoryDetailUI>

    fun getDataByDataSource(dataSource: ProductSliderFragment.DataSource) {
        when(dataSource) {
            is ProductSliderFragment.DataSource.Request -> updateCategorySlider(dataSource.sliderType)
            is ProductSliderFragment.DataSource.Args -> sliderCategoryUIListMLD.value = dataSource.categoryDetailUIList
        }
    }

    private fun updateCategorySlider(sliderType: String) {
        Log.i(LogSettings.ID_LOG, sliderType)
        getProductsSliderByType(sliderType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    Log.i(LogSettings.ID_LOG, "SUC $sliderType")
                    when(response) {
                        is ResponseEntity.Success -> {
                            response.data?.let { noNullData ->
                                categoryDetailUIList = noNullData.mapToUI()
                                sliderCategoryUIListMLD.value = categoryDetailUIList
                            }
                        }
                        is ResponseEntity.Error -> stateHideMLD.value = true
                    }
                },
                onError = { throwable -> Log.i(LogSettings.ID_LOG, "ERROR ${throwable.message}") }
            )
    }


    private fun getProductsSliderByType(sliderType: String) = when(sliderType) {
        DISCOUNT_PRODUCTS_SLIDER -> dataRepository.fetchDiscountsSlider()
        NOVELTIES_PRODUCTS_SLIDER -> dataRepository.fetchNoveltiesSlider()
        TOP_PRODUCTS_SLIDER -> dataRepository.fetchTopSlider()
        BOTTOM_PRODUCTS_SLIDER -> dataRepository.fetchBottomSlider()
        VIEWED_PRODUCTS_SLIDER -> dataRepository.fetchViewedProductSlider(dataRepository.fetchUserId())
        else -> throw Exception()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}