package com.vodovoz.app.ui.components.fragment.slider.product_slider

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
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

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val categoryDetailUIListMLD = MutableLiveData<List<CategoryDetailUI>>()
    private val errorMessageMLD = MutableLiveData<String>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val categoryDetailUIListLD: LiveData<List<CategoryDetailUI>> = categoryDetailUIListMLD
    val errorMessageLD: LiveData<String> = errorMessageMLD

    private val compositeDisposable = CompositeDisposable()

    lateinit var categoryDetailUIList: List<CategoryDetailUI>
    private lateinit var dataSource: ProductSliderFragment.DataSource

    fun updateArgs(dataSource: ProductSliderFragment.DataSource) {
        this.dataSource = dataSource
        updateData()
    }

    fun updateData() {
        when(dataSource) {
            is ProductSliderFragment.DataSource.Request -> {
                getProductsSliderByType()
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onSuccess = { response ->
                            Log.i(LogSettings.REQ_RES_LOG, "READY PRODUCT")
                            when(response) {
                                is ResponseEntity.Success -> {
                                    viewStateMLD.value = ViewState.Success()
                                    categoryDetailUIList = response.data.mapToUI()
                                    categoryDetailUIListMLD.value = categoryDetailUIList
                                }
                                is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                                is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                            }
                        },
                        onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message!!) }
                    ).addTo(compositeDisposable)
            }
            is ProductSliderFragment.DataSource.Args -> {
                categoryDetailUIListMLD.value = (dataSource as ProductSliderFragment.DataSource.Args).categoryDetailUIList
            }
        }
    }


    private fun getProductsSliderByType() = when((dataSource as ProductSliderFragment.DataSource.Request).sliderType) {
        DISCOUNT_PRODUCTS_SLIDER -> dataRepository.fetchDiscountsSlider()
        NOVELTIES_PRODUCTS_SLIDER -> dataRepository.fetchNoveltiesSlider()
        TOP_PRODUCTS_SLIDER -> dataRepository.fetchTopSlider()
        BOTTOM_PRODUCTS_SLIDER -> dataRepository.fetchBottomSlider()
        VIEWED_PRODUCTS_SLIDER -> dataRepository.fetchViewedProductSlider(dataRepository.fetchUserId())
        else -> throw Exception()
    }


    fun changeCart(productUI: ProductUI) {

    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}