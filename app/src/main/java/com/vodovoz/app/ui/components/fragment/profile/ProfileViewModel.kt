package com.vodovoz.app.ui.components.fragment.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.mapper.UserDataMapper.mapToUI
import com.vodovoz.app.ui.model.UserDataUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
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

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val isAlreadyLoginLD: LiveData<Boolean> = isAlreadyLoginMLD
    val userDataUILD: LiveData<UserDataUI> = userDataUIMLD

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
        dataRepository
            .fetchUserData(userId = dataRepository.fetchUserId()!!)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                        is ResponseEntity.Success -> {
                            userDataUIMLD.value = response.data.mapToUI()
                            viewStateMLD.value = ViewState.Success()
                        }
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