package com.vodovoz.app.ui.components.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.ui.components.base.FetchState
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy

class HomeViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val fetchStateMLD = MutableLiveData<FetchState<Any>>()
    val fetchStateLD: LiveData<FetchState<Any>> = fetchStateMLD

    var lastFetchState: FetchState<Any>? = null
        set(value) {
            field = value
            fetchStateMLD.value = field
        }

    init { updateData() }

    fun updateData() {
        val firstRequest  = Single.zip(
            dataRepository.updateBrandList(),
            dataRepository.updateCommentList(),
            dataRepository.updatePromotionsList(),
            dataRepository.fetchViewedProductSlider(dataRepository.fetchUserId() ?: 0)
        ) { _, _, _, _ ->
            Single.just(true)
        }

        val secondRequest = Single.zip(
            dataRepository.updateMainBannerList(),
            dataRepository.updateHistoryList(),
            dataRepository.updatePopularSectionList(),
            dataRepository.updateDiscountProductList(),
            dataRepository.updateSecondaryBannerList(),
            dataRepository.updateNoveltiesProductList(),
            dataRepository.updateCountryList(),
            dataRepository.updateDoubleCategory()
        ) { _, _, _, _, _, _, _, _  ->
            Single.just(true)
        }

        Single.zip(firstRequest, secondRequest) { _, _ ->
            Single.just(true)
        }.doOnSubscribe {
            lastFetchState = FetchState.Loading()
        }.subscribeBy(
            onSuccess = { lastFetchState = FetchState.Success() },
            onError = { lastFetchState = FetchState.Error(it.message!!) }
        )
    }

}