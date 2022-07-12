package com.vodovoz.app.ui.components.fragment.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.OrderEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.UserDataEntity
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.mapper.OrderMapper.mapToUI
import com.vodovoz.app.mapper.UserDataMapper.mapToUI
import com.vodovoz.app.ui.model.OrderUI
import com.vodovoz.app.ui.model.UserDataUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class ProfileViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val userDataUIMLD = MutableLiveData<UserDataUI>()
    private val isAlreadyLoginMLD = MutableLiveData<Boolean>()
    private val orderUIListMLD = MutableLiveData<List<OrderUI>>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val isAlreadyLoginLD: LiveData<Boolean> = isAlreadyLoginMLD
    val userDataUILD: LiveData<UserDataUI> = userDataUIMLD
    val orderUIListLD: LiveData<List<OrderUI>> = orderUIListMLD

    private val compositeDisposable = CompositeDisposable()

    fun isAlreadyLogin() {
        when(dataRepository.isAlreadyLogin()) {
            true -> {
                isAlreadyLoginMLD.value = true
                updateData()
            }
            else -> isAlreadyLoginMLD.value = false
        }
    }

    fun updateData() {

        Single.zip(
            dataRepository.fetchUserData(userId = dataRepository.fetchUserId()!!),
            dataRepository.fetchOrdersSlider()
        ) { userDataResponse, ordersResponse ->
            Pair(userDataResponse, ordersResponse)
        }.subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { pair ->
                    if (pair.first is ResponseEntity.Success && pair.second is ResponseEntity.Success) {
                        userDataUIMLD.value = (pair.first as ResponseEntity.Success<UserDataEntity>).data.mapToUI()
                        orderUIListMLD.value = (pair.second as ResponseEntity.Success<List<OrderEntity>>).data.mapToUI()
                        viewStateMLD.value = ViewState.Success()
                    } else if (pair.first is ResponseEntity.Success && pair.second is ResponseEntity.Hide) {
                        userDataUIMLD.value = (pair.first as ResponseEntity.Success<UserDataEntity>).data.mapToUI()
                        viewStateMLD.value = ViewState.Success()
                    } else {
                        viewStateMLD.value = ViewState.Error("Ошибка!")
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message!!) }
            ).addTo(compositeDisposable)
    }

    fun logout() {
        dataRepository.logout().subscribe()
        isAlreadyLoginMLD.value = false
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}