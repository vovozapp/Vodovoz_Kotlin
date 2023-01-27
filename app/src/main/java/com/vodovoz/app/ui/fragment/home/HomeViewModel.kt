package com.vodovoz.app.ui.fragment.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.BannerMapper.mapToUI
import com.vodovoz.app.mapper.BrandMapper.mapToUI
import com.vodovoz.app.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.mapper.CommentMapper.mapToUI
import com.vodovoz.app.mapper.CountriesSliderBundleMapper.mapToUI
import com.vodovoz.app.mapper.HistoryMapper.mapToUI
import com.vodovoz.app.mapper.OrderMapper.mapToUI
import com.vodovoz.app.mapper.PopupNewsMapper.mapToUI
import com.vodovoz.app.mapper.PromotionMapper.mapToUI
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.model.*
import com.vodovoz.app.ui.model.custom.CountriesSliderBundleUI
import com.vodovoz.app.ui.model.custom.PromotionsSliderBundleUI
import com.vodovoz.app.util.LogSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {


    private val viewStateMLD = MutableLiveData<ViewState>()
    private val errorMLD = MutableLiveData<String>()
    private val advertisingBannersSliderDataMLD = MutableLiveData<List<BannerUI>>()
    private val advertisingBannersSliderHideMLD = MutableLiveData<Boolean>()
    private val historiesSliderDataMLD = MutableLiveData<List<HistoryUI>>()
    private val historiesSliderHideMLD = MutableLiveData<Boolean>()
    private val popularCategoriesSliderDataMLD = MutableLiveData<List<CategoryUI>>()
    private val popularCategoriesSliderHideMLD = MutableLiveData<Boolean>()
    private val categoryBannersSliderDataMLD = MutableLiveData<List<BannerUI>>()
    private val categoryBannersSliderHideMLD = MutableLiveData<Boolean>()
    private val discountProductsSliderDataMLD = MutableLiveData<List<CategoryDetailUI>>()
    private val discountProductsSliderHideMLD = MutableLiveData<Boolean>()
    private val topProductsSliderDataMLD = MutableLiveData<List<CategoryDetailUI>>()
    private val topProductsSliderHideMLD = MutableLiveData<Boolean>()
    private val ordersSliderDataMLD = MutableLiveData<List<OrderUI>>()
    private val ordersSliderHideMLD = MutableLiveData<Boolean>()
    private val noveltiesProductsSliderDataMLD = MutableLiveData<List<CategoryDetailUI>>()
    private val noveltiesProductsSliderHideMLD = MutableLiveData<Boolean>()
    private val promotionsSliderDataMLD = MutableLiveData<PromotionsSliderBundleUI>()
    private val promotionsSliderHideMLD = MutableLiveData<Boolean>()
    private val bottomProductsSliderDataMLD = MutableLiveData<List<CategoryDetailUI>>()
    private val bottomProductsSliderHideMLD = MutableLiveData<Boolean>()
    private val brandsSliderDataMLD = MutableLiveData<List<BrandUI>>()
    private val brandsSliderHideMLD = MutableLiveData<Boolean>()
    private val countriesSliderDataMLD = MutableLiveData<CountriesSliderBundleUI>()
    private val countriesSliderHideMLD = MutableLiveData<Boolean>()
    private val viewedProductsSliderDataMLD = MutableLiveData<List<CategoryDetailUI>>()
    private val viewedProductsSliderHideMLD = MutableLiveData<Boolean>()
    private val commentsSliderDataMLD = MutableLiveData<List<CommentUI>>()
    private val commentsSliderHideMLD = MutableLiveData<Boolean>()
    private var popupNewsUIMLD = MutableLiveData<PopupNewsUI>()


    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val errorLD: LiveData<String> = errorMLD
    val advertisingBannersSliderDataLD: LiveData<List<BannerUI>> = advertisingBannersSliderDataMLD
    val advertisingBannersSliderHideLD: LiveData<Boolean> = advertisingBannersSliderHideMLD
    val historiesSliderDataLD: LiveData<List<HistoryUI>> = historiesSliderDataMLD
    val historiesSliderHideLD: LiveData<Boolean> = historiesSliderHideMLD
    val popularCategoriesSliderDataLD: LiveData<List<CategoryUI>> = popularCategoriesSliderDataMLD
    val popularCategoriesSliderHideLD: LiveData<Boolean> = popularCategoriesSliderHideMLD
    val categoryBannersSliderDataLD: LiveData<List<BannerUI>> = categoryBannersSliderDataMLD
    val categoryBannersSliderHideLD: LiveData<Boolean> = categoryBannersSliderHideMLD
    val discountProductsSliderDataLD: LiveData<List<CategoryDetailUI>> = discountProductsSliderDataMLD
    val discountProductsSliderHideLD: LiveData<Boolean> = discountProductsSliderHideMLD
    val topProductsSliderDataLD: LiveData<List<CategoryDetailUI>> = topProductsSliderDataMLD
    val topProductsSliderHideLD: LiveData<Boolean> = topProductsSliderHideMLD
    val ordersSliderDataLD: LiveData<List<OrderUI>> = ordersSliderDataMLD
    val ordersSliderHideLD: LiveData<Boolean> = ordersSliderHideMLD
    val noveltiesProductsSliderDataLD: LiveData<List<CategoryDetailUI>> = noveltiesProductsSliderDataMLD
    val noveltiesProductsSliderHideLD: LiveData<Boolean> = noveltiesProductsSliderHideMLD
    val promotionsSliderDataLD: LiveData<PromotionsSliderBundleUI> = promotionsSliderDataMLD
    val promotionsSliderHideLD: LiveData<Boolean> = promotionsSliderHideMLD
    val bottomProductsSliderDataLD: LiveData<List<CategoryDetailUI>> = bottomProductsSliderDataMLD
    val bottomProductsSliderHideLD: LiveData<Boolean> = bottomProductsSliderHideMLD
    val brandsSliderDataLD: LiveData<List<BrandUI>> = brandsSliderDataMLD
    val brandsSliderHideLD: LiveData<Boolean> = brandsSliderHideMLD
    val countriesSliderDataLD: LiveData<CountriesSliderBundleUI> = countriesSliderDataMLD
    val countriesSliderHideLD: LiveData<Boolean> = countriesSliderHideMLD
    val viewedProductsSliderDataLD: LiveData<List<CategoryDetailUI>> = viewedProductsSliderDataMLD
    val viewedProductsSliderHideLD: LiveData<Boolean> = viewedProductsSliderHideMLD
    val commentsSliderDataLD: LiveData<List<CommentUI>> = commentsSliderDataMLD
    val commentsSliderHideLD: LiveData<Boolean> = commentsSliderHideMLD
    val popupNewsUILD: LiveData<PopupNewsUI> = popupNewsUIMLD

    private val compositeDisposable = CompositeDisposable()

    private val advertisingBannersSliderUpdateSubject: PublishSubject<ViewState> =
        PublishSubject.create()
    private val historiesSliderUpdateSubject: PublishSubject<ViewState> = PublishSubject.create()
    private val popularCategoriesSliderUpdateSubject: PublishSubject<ViewState> =
        PublishSubject.create()
    private val categoryBannersSliderUpdateSubject: PublishSubject<ViewState> =
        PublishSubject.create()
    private val discountProductsSliderUpdateSubject: PublishSubject<ViewState> =
        PublishSubject.create()
    private val topProductsSliderUpdateSubject: PublishSubject<ViewState> = PublishSubject.create()
    private val noveltiesProductsSliderUpdateSubject: PublishSubject<ViewState> =
        PublishSubject.create()
    private val ordersSliderUpdateSubject: PublishSubject<ViewState> = PublishSubject.create()
    private val promotionsSliderUpdateSubject: PublishSubject<ViewState> = PublishSubject.create()
    private val bottomProductsSliderUpdateSubject: PublishSubject<ViewState> =
        PublishSubject.create()
    private val brandsSliderUpdateSubject: PublishSubject<ViewState> = PublishSubject.create()
    private val countriesSliderUpdateSubject: PublishSubject<ViewState> = PublishSubject.create()
    private val viewedProductsSliderUpdateSubject: PublishSubject<ViewState> =
        PublishSubject.create()
    private val commentsSliderUpdateSubject: PublishSubject<ViewState> = PublishSubject.create()

    var isShowPopupNews = true
    private var isUpdateSuccess = false

    fun updateArgs(isShowPopupNews: Boolean) {
        this.isShowPopupNews = isShowPopupNews
    }

    init {
        Observable.zip(listOf(
            advertisingBannersSliderUpdateSubject,
            historiesSliderUpdateSubject,
            popularCategoriesSliderUpdateSubject,
            categoryBannersSliderUpdateSubject,
            discountProductsSliderUpdateSubject,
            topProductsSliderUpdateSubject,
            ordersSliderUpdateSubject,
            noveltiesProductsSliderUpdateSubject,
            promotionsSliderUpdateSubject,
            bottomProductsSliderUpdateSubject,
            brandsSliderUpdateSubject,
            countriesSliderUpdateSubject,
            viewedProductsSliderUpdateSubject,
            commentsSliderUpdateSubject
        )) { objectList ->
            mutableListOf<ViewState>().apply {
                objectList.forEach { obj -> add(obj as ViewState) }
            }
        }.flatMap { viewStateList ->
            Observable.just(when (val error = viewStateList.find { it is ViewState.Error }) {
                null -> viewStateList.first()
                else -> error
            })
        }.subscribeBy(
            onNext = { viewState ->
                when(viewState) {
                    is ViewState.Error -> {
                        if (isUpdateSuccess) errorMLD.value = viewState.errorMessage
                        else viewStateMLD.value = viewState
                    }
                    is ViewState.Success -> {
                        isUpdateSuccess = true
                        viewStateMLD.value = viewState
                        if (isShowPopupNews) {
                            updatePopupNews()
                        }
                    }
                    else -> viewStateMLD.value = viewState
                }
                viewStateMLD.value = viewState
            },
            onError = { throwable ->
                if (isUpdateSuccess) errorMLD.value = throwable.message ?: "Неизвестная ошибка"
                else viewStateMLD.value = ViewState.Error(throwable.message ?: "Неизвестная ошибка")
            }
        ).addTo(compositeDisposable)
    }

    fun updateData() {
        if (!isUpdateSuccess) viewStateMLD.value = ViewState.Loading()

        updateAdvertisingBannersSlider()
        updateHistoriesSlider()
        updatePopularCategoriesSlider()
        updateCategoryBannersSlider()
        updateDiscountProductsSlider()
        updateTopProductsSlider()
        updateOrdersSlider()
        updateNoveltiesProductsSlider()
        updatePromotionsSlider()
        updateBottomProductsSlider()
        updateBrandsSlider()
        updateCountriesSlider()
        updateViewedProductsSlider()
        updateCommentsSlider()
    }

    private fun updateAdvertisingBannersSlider() {
        dataRepository.fetchAdvertisingBannersSlider()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when (response) {
                        is ResponseEntity.Hide -> {
                            advertisingBannersSliderHideMLD.value = true
                            advertisingBannersSliderUpdateSubject.onNext(ViewState.Success())
                        }
                        is ResponseEntity.Error -> {
                            advertisingBannersSliderHideMLD.value = true
                            advertisingBannersSliderUpdateSubject.onNext(ViewState.Error(response.errorMessage))
                        }
                        is ResponseEntity.Success -> {
                            categoryBannersSliderHideMLD.value = false
                            advertisingBannersSliderDataMLD.value = response.data.mapToUI()
                            advertisingBannersSliderUpdateSubject.onNext(ViewState.Success())
                        }
                    }
                },
                onError = { throwable ->
                    Log.d(LogSettings.HOME_LOG, "Error Advertising banners slider")
                    advertisingBannersSliderUpdateSubject.onNext(
                        ViewState.Error(throwable.message ?: "Неизвестная ошибка")
                    )
                }
            )
    }

    private fun updateHistoriesSlider() {
        dataRepository.fetchHistoriesSlider()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when (response) {
                        is ResponseEntity.Hide -> {
                            historiesSliderHideMLD.value = true
                            historiesSliderUpdateSubject.onNext(ViewState.Success())
                        }
                        is ResponseEntity.Error -> {
                            historiesSliderHideMLD.value = true
                            historiesSliderUpdateSubject.onNext(ViewState.Error(response.errorMessage))
                        }
                        is ResponseEntity.Success -> {
                            historiesSliderHideMLD.value = false
                            historiesSliderDataMLD.value = response.data.mapToUI()
                            historiesSliderUpdateSubject.onNext(ViewState.Success())
                        }
                    }
                },
                onError = { throwable ->
                    historiesSliderUpdateSubject.onNext(
                        ViewState.Error(throwable.message ?: "Неизвестная ошибка")
                    )
                }
            ).addTo(compositeDisposable)
    }

    private fun updatePopularCategoriesSlider() {
        dataRepository.fetchPopularCategoriesSlider()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when (response) {
                        is ResponseEntity.Hide -> {
                            popularCategoriesSliderHideMLD.value = true
                            popularCategoriesSliderUpdateSubject.onNext(ViewState.Success())
                        }
                        is ResponseEntity.Error -> {
                            popularCategoriesSliderHideMLD.value = true
                            popularCategoriesSliderUpdateSubject.onNext(ViewState.Error(response.errorMessage))
                        }
                        is ResponseEntity.Success -> {
                            popularCategoriesSliderHideMLD.value = false
                            popularCategoriesSliderDataMLD.value = response.data.mapToUI()
                            popularCategoriesSliderUpdateSubject.onNext(ViewState.Success())
                        }
                    }
                },
                onError = { throwable ->
                    popularCategoriesSliderUpdateSubject.onNext(
                        ViewState.Error(throwable.message ?: "Неизвестная ошибка")
                    )
                }
            ).addTo(compositeDisposable)
    }

    private fun updateCategoryBannersSlider() {
        dataRepository.fetchCategoryBannersSlider()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when (response) {
                        is ResponseEntity.Hide -> {
                            categoryBannersSliderHideMLD.value = true
                            categoryBannersSliderUpdateSubject.onNext(ViewState.Success())
                        }
                        is ResponseEntity.Error -> {
                            categoryBannersSliderHideMLD.value = true
                            categoryBannersSliderUpdateSubject.onNext(ViewState.Error(response.errorMessage))
                        }
                        is ResponseEntity.Success -> {
                            categoryBannersSliderHideMLD.value = false
                            categoryBannersSliderDataMLD.value = response.data.mapToUI()
                            categoryBannersSliderUpdateSubject.onNext(ViewState.Success())
                        }
                    }
                },
                onError = { throwable ->
                    categoryBannersSliderUpdateSubject.onNext(
                        ViewState.Error(throwable.message ?: "Неизвестная ошибка")
                    )
                }
            )
    }

    private fun updateDiscountProductsSlider() {
        dataRepository.fetchDiscountProductsSlider()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when (response) {
                        is ResponseEntity.Hide -> {
                            discountProductsSliderHideMLD.value = true
                            discountProductsSliderUpdateSubject.onNext(ViewState.Success())
                        }
                        is ResponseEntity.Error -> {
                            discountProductsSliderHideMLD.value = true
                            discountProductsSliderUpdateSubject.onNext(ViewState.Error(response.errorMessage))
                        }
                        is ResponseEntity.Success -> {
                            discountProductsSliderHideMLD.value = false
                            discountProductsSliderDataMLD.value = response.data.mapToUI()
                            discountProductsSliderUpdateSubject.onNext(ViewState.Success())
                        }
                    }
                },
                onError = { throwable ->
                    discountProductsSliderUpdateSubject.onNext(
                        ViewState.Error(throwable.message ?: "Неизвестная ошибка")
                    )
                }
            )
    }

    private fun updateTopProductsSlider() {
        dataRepository.fetchTopProductsSlider()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when (response) {
                        is ResponseEntity.Hide -> {
                            topProductsSliderHideMLD.value = true
                            topProductsSliderUpdateSubject.onNext(ViewState.Success())
                        }
                        is ResponseEntity.Error -> {
                            topProductsSliderHideMLD.value = true
                            topProductsSliderUpdateSubject.onNext(ViewState.Error(response.errorMessage))
                        }
                        is ResponseEntity.Success -> {
                            topProductsSliderHideMLD.value = false
                            topProductsSliderDataMLD.value = response.data.mapToUI()
                            topProductsSliderUpdateSubject.onNext(ViewState.Success())
                        }
                    }
                },
                onError = { throwable ->
                    topProductsSliderUpdateSubject.onNext(
                        ViewState.Error(throwable.message ?: "Неизвестная ошибка")
                    )
                }
            )
    }

    private fun updateOrdersSlider() {
        dataRepository.fetchOrdersSlider()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when (response) {
                        is ResponseEntity.Hide -> {
                            ordersSliderHideMLD.value = true
                            ordersSliderUpdateSubject.onNext(ViewState.Success())
                        }
                        is ResponseEntity.Error -> {
                            ordersSliderHideMLD.value = true
                            ordersSliderUpdateSubject.onNext(ViewState.Error(response.errorMessage))
                        }
                        is ResponseEntity.Success -> {
                            ordersSliderHideMLD.value = false
                            ordersSliderDataMLD.value = response.data.mapToUI()
                            ordersSliderUpdateSubject.onNext(ViewState.Success())
                        }
                    }
                },
                onError = { throwable ->
                    ordersSliderUpdateSubject.onNext(
                        ViewState.Error(throwable.message ?: "Неизвестная ошибка")
                    )
                }
            ).addTo(compositeDisposable)
    }

    private fun updateNoveltiesProductsSlider() {
        dataRepository.fetchNoveltiesProductsSlider()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when (response) {
                        is ResponseEntity.Hide -> {
                            noveltiesProductsSliderHideMLD.value = true
                            noveltiesProductsSliderUpdateSubject.onNext(ViewState.Success())
                        }
                        is ResponseEntity.Error -> {
                            noveltiesProductsSliderHideMLD.value = true
                            noveltiesProductsSliderUpdateSubject.onNext(ViewState.Error(response.errorMessage))
                        }
                        is ResponseEntity.Success -> {
                            noveltiesProductsSliderHideMLD.value = false
                            noveltiesProductsSliderDataMLD.value = response.data.mapToUI()
                            noveltiesProductsSliderUpdateSubject.onNext(ViewState.Success())
                        }
                    }
                },
                onError = { throwable ->
                    noveltiesProductsSliderUpdateSubject.onNext(
                        ViewState.Error(throwable.message ?: "Неизвестная ошибка")
                    )
                }
            )
    }

    private fun updatePromotionsSlider() {
        dataRepository.fetchPromotionsSlider()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when (response) {
                        is ResponseEntity.Hide -> {
                            promotionsSliderHideMLD.value = true
                            promotionsSliderUpdateSubject.onNext(ViewState.Success())
                        }
                        is ResponseEntity.Error -> {
                            promotionsSliderHideMLD.value = true
                            promotionsSliderUpdateSubject.onNext(ViewState.Error(response.errorMessage))
                        }
                        is ResponseEntity.Success -> {
                            promotionsSliderHideMLD.value = false
                            promotionsSliderDataMLD.value = PromotionsSliderBundleUI(
                                title = "Акции",
                                containShowAllButton = true,
                                promotionUIList = response.data.mapToUI()
                            )
                            promotionsSliderUpdateSubject.onNext(ViewState.Success())
                        }
                    }
                },
                onError = { throwable ->
                    promotionsSliderUpdateSubject.onNext(
                        ViewState.Error(throwable.message ?: "Неизвестная ошибка")
                    )
                }
            ).addTo(compositeDisposable)
    }

    private fun updateBottomProductsSlider() {
        dataRepository.fetchBottomProductsSlider()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when (response) {
                        is ResponseEntity.Hide -> {
                            bottomProductsSliderHideMLD.value = true
                            bottomProductsSliderUpdateSubject.onNext(ViewState.Success())
                        }
                        is ResponseEntity.Error -> {
                            bottomProductsSliderHideMLD.value = true
                            bottomProductsSliderUpdateSubject.onNext(ViewState.Error(response.errorMessage))
                        }
                        is ResponseEntity.Success -> {
                            bottomProductsSliderHideMLD.value = false
                            bottomProductsSliderDataMLD.value = response.data.mapToUI()
                            bottomProductsSliderUpdateSubject.onNext(ViewState.Success())
                        }
                    }
                },
                onError = { throwable ->
                    bottomProductsSliderUpdateSubject.onNext(
                        ViewState.Error(throwable.message ?: "Неизвестная ошибка")
                    )
                }
            )
    }

    private fun updateBrandsSlider() {
        dataRepository.fetchBrandsSlider()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when (response) {
                        is ResponseEntity.Hide -> {
                            brandsSliderHideMLD.value = true
                            brandsSliderUpdateSubject.onNext(ViewState.Success())
                        }
                        is ResponseEntity.Error -> {
                            brandsSliderHideMLD.value = true
                            brandsSliderUpdateSubject.onNext(ViewState.Error(response.errorMessage))
                        }
                        is ResponseEntity.Success -> {
                            brandsSliderHideMLD.value = false
                            brandsSliderDataMLD.value = response.data.mapToUI()
                            brandsSliderUpdateSubject.onNext(ViewState.Success())
                        }
                    }
                },
                onError = { throwable ->
                    brandsSliderUpdateSubject.onNext(
                        ViewState.Error(throwable.message ?: "Неизвестная ошибка")
                    )
                }
            )
    }

    private fun updateCountriesSlider() {
        dataRepository.fetchCountriesSlider()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when (response) {
                        is ResponseEntity.Hide -> {
                            countriesSliderHideMLD.value = true
                            countriesSliderUpdateSubject.onNext(ViewState.Success())
                        }
                        is ResponseEntity.Error -> {
                            countriesSliderHideMLD.value = true
                            countriesSliderUpdateSubject.onNext(ViewState.Error(response.errorMessage))
                        }
                        is ResponseEntity.Success -> {
                            countriesSliderHideMLD.value = false
                            countriesSliderDataMLD.value = response.data.mapToUI()
                            countriesSliderUpdateSubject.onNext(ViewState.Success())
                        }
                    }
                },
                onError = { throwable ->
                    countriesSliderUpdateSubject.onNext(
                        ViewState.Error(throwable.message ?: "Неизвестная ошибка")
                    )
                }
            ).addTo(compositeDisposable)
    }

    private fun updateViewedProductsSlider() {
        dataRepository.fetchViewedProductsSlider()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    Log.d(LogSettings.HOME_LOG, "On get response")
                    when (response) {
                        is ResponseEntity.Hide -> {
                            Log.d(LogSettings.HOME_LOG, "Hide")
                            viewedProductsSliderHideMLD.value = true
                            viewedProductsSliderUpdateSubject.onNext(ViewState.Success())
                        }
                        is ResponseEntity.Error -> {
                            Log.d(LogSettings.HOME_LOG, "Error")
                            viewedProductsSliderHideMLD.value = true
                            viewedProductsSliderUpdateSubject.onNext(ViewState.Error(response.errorMessage))
                        }
                        is ResponseEntity.Success -> {
                            viewedProductsSliderHideMLD.value = false
                            viewedProductsSliderDataMLD.value = response.data.mapToUI()
                            viewedProductsSliderUpdateSubject.onNext(ViewState.Success())
                        }
                    }
                },
                onError = { throwable ->
                    Log.d(LogSettings.HOME_LOG, "Error viewed products slider")
                    viewedProductsSliderUpdateSubject.onNext(
                        ViewState.Error(throwable.message ?: "Неизвестная ошибка")
                    )
                }
            )
    }

    private fun updateCommentsSlider() {
        dataRepository.fetchCommentsSlider()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when (response) {
                        is ResponseEntity.Hide -> {
                            commentsSliderHideMLD.value = true
                            commentsSliderUpdateSubject.onNext(ViewState.Success())
                        }
                        is ResponseEntity.Error -> {
                            commentsSliderHideMLD.value = true
                            countriesSliderUpdateSubject.onNext(ViewState.Error(response.errorMessage))
                        }
                        is ResponseEntity.Success -> {
                            commentsSliderHideMLD.value = false
                            commentsSliderDataMLD.value = response.data.mapToUI()
                            commentsSliderUpdateSubject.onNext(ViewState.Success())
                        }
                    }
                },
                onError = { throwable ->
                    Log.d(LogSettings.HOME_LOG, "Error comment slider")
                    commentsSliderUpdateSubject.onNext(
                        ViewState.Error(throwable.message ?: "Неизвестная ошибка")
                    )
                }
            ).addTo(compositeDisposable)
    }

    private fun updatePopupNews() {
        dataRepository
            .fetchPopupNews()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when (response) {
                        is ResponseEntity.Success -> popupNewsUIMLD.value = response.data.mapToUI()
                        else -> {}
                    }
                },
                onError = {}
            ).addTo(compositeDisposable)
    }

    fun changeFavoriteStatus(productId: Long, isFavorite: Boolean) {
        when(isFavorite) {
            true -> dataRepository.addToFavorite(productId)
            false -> dataRepository.removeFromFavorite(productId = productId)

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {},
                onError = { throwable -> errorMLD.value = throwable.message ?: "Неизвестная ошибка" }
            ).addTo(compositeDisposable)
    }

    fun changeCart(productId: Long, quantity: Int) {
        dataRepository
            .changeCart(
                productId = productId,
                quantity = quantity
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {},
                onError = { throwable ->
                    errorMLD.value = throwable.message ?: "Неизвестная ошибка"
                }
            )
    }

    fun isLoginAlready() = dataRepository.isAlreadyLogin()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}