package com.vodovoz.app.ui.fragment.user_data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.UserDataMapper.mapToUI
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.model.UserDataUI
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class UserDataViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val userDataUIMLD = MutableLiveData<UserDataUI>()
    private val messageMLD = MutableLiveData<String>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val userDataUILD: LiveData<UserDataUI> = userDataUIMLD
    val messageLD: LiveData<String> = messageMLD

    lateinit var userDataUI: UserDataUI

    private val compositeDisposable = CompositeDisposable()

    var validEmail = false
    var validPhone = false
    var validFirstName = false
    var validSecondName = false
    var validPassword = false

    var canChangeBirthday = true

    fun updateData() {
        dataRepository
            .fetchUserData(userId = dataRepository.fetchUserId()!!)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                        is ResponseEntity.Success -> {
                            userDataUI = response.data.mapToUI()
                            canChangeBirthday = userDataUI.birthday == "Не указано"
                            userDataUIMLD.value = userDataUI
                            viewStateMLD.value = ViewState.Success()
                        }
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message!!) }
            ).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun updateUserData(
        firstName: String? = null,
        secondName: String? = null,
        gender: String? = null,
        birthday: String? = null,
        email: String? = null,
        phone: String? = null,
        password: String? = null
    ) {
        dataRepository
            .updateUserData(
                firstName = firstName,
                secondName = secondName,
                sex = gender,
                birthday = birthday,
                phone = phone,
                email = email,
                password = password
            )
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Success() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Success -> {
                            if (birthday?.isNotEmpty() == true) canChangeBirthday = false
                            messageMLD.value = "Данные успешно изменены"
                        }
                        is ResponseEntity.Hide -> messageMLD.value = "Неизвестная ошибка"
                        is ResponseEntity.Error -> messageMLD.value = response.errorMessage
                    }
                },
                onError = { throwable ->
                    messageMLD.value = throwable.message ?: "Неизвестная ошибка"
                }
            ).addTo(compositeDisposable)
    }

}