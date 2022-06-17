package com.vodovoz.app.ui.components.fragment.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.mapper.UserDataMapper.mapToUI
import com.vodovoz.app.ui.model.UserDataUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class AccountViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val fetchStateMLD = MutableLiveData<FetchState<UserDataUI>>()
    private val isAlreadyLoginMLD = MutableLiveData<Boolean>()

    val fetchStateLD: LiveData<FetchState<UserDataUI>> = fetchStateMLD
    val isAlreadyLoginLD: LiveData<Boolean> = isAlreadyLoginMLD

    private var isAlreadyLogin = false

    fun isAlreadyLogin() {
        isAlreadyLogin = dataRepository.isAlreadyLogin()
        when(isAlreadyLogin) {
            true -> {
                isAlreadyLoginMLD.value = true
                updateData()
            }
            else -> isAlreadyLoginMLD.value = false
        }
    }

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
        )

    fun logout() {
        dataRepository.logout()
        isAlreadyLoginMLD.value = false
    }

}