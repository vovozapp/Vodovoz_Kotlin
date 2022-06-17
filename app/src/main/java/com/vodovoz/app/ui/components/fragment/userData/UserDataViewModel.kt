package com.vodovoz.app.ui.components.fragment.userData

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.mapper.UserDataMapper.mapToUI
import com.vodovoz.app.ui.model.UserDataUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class UserDataViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val fetchStateMLD = MutableLiveData<FetchState<UserDataUI>>()
    val fetchStateLD: LiveData<FetchState<UserDataUI>> = fetchStateMLD

    private val compositeDisposable = CompositeDisposable()

    init { updateData() }

    fun updateData() = dataRepository
        .fetchUserData(userId = dataRepository.fetchUserId()!!)
        .subscribeOn(Schedulers.io())
        .doOnSubscribe { fetchStateMLD.value = FetchState.Loading() }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onSuccess = { response ->
                fetchStateMLD.value = FetchState.Success(response.data?.mapToUI())
            },
            onError = { throwable -> fetchStateMLD.value = FetchState.Error(throwable.message) }
        ).addTo(compositeDisposable)

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}