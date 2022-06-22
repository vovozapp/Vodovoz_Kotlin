package com.vodovoz.app.ui.components.fragment.promotionDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.mapper.PromotionDetailMapper.mapToUI
import com.vodovoz.app.ui.model.PromotionDetailUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class PromotionDetailViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val fetchStateMLD = MutableLiveData<FetchState<PromotionDetailUI>>()
    val fetchStateLD: LiveData<FetchState<PromotionDetailUI>> = fetchStateMLD

    private val compositeDisposable = CompositeDisposable()

    var promotionId: Long? = null

    fun setArgs(promotionId: Long) {
        this.promotionId = promotionId
        updateData()
    }

    fun updateData() {
        promotionId?.let {
            dataRepository
                .fetchPromotionDetails(promotionId = promotionId!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = { response -> fetchStateMLD.value = FetchState.Success(response.data?.mapToUI()) },
                    onError = { throwable -> fetchStateMLD.value = FetchState.Error(throwable.message) }
                ).addTo(compositeDisposable)
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}