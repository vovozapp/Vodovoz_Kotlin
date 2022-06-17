package com.vodovoz.app.ui.components.fragment.orderSlider

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.mapper.OrderMapper.mapToUI
import com.vodovoz.app.ui.model.OrderUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class OrderSliderViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val fetchStateMLD = MutableLiveData<FetchState<List<OrderUI>>>()
    val fetchStateLD: LiveData<FetchState<List<OrderUI>>> = fetchStateMLD

    private val compositeDisposable = CompositeDisposable()

    init { updateData() }

    fun updateData() {
        when(val userId = dataRepository.fetchUserId()) {
            null -> fetchStateMLD.value = FetchState.Hide()
            else -> dataRepository.fetchOrderSlider(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy { response ->
                    when(response) {
                        is ResponseEntity.Success -> fetchStateMLD.value = FetchState.Success(response.data?.mapToUI())
                        is ResponseEntity.Error -> fetchStateMLD.value = FetchState.Hide()
                    }
                }
        }
    }
    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}