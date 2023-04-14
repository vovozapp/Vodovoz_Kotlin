package com.vodovoz.app.feature.auth.reg.old

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.config.AuthConfig.EMAIL_IS_ALREADY_REGISTERED
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.util.FieldValidationsSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val errorMLD = MutableLiveData<String>()
    private val isRegisterSuccessMLD = MutableLiveData<Boolean>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val errorLD: LiveData<String> = errorMLD
    val isRegisterSuccessLD: LiveData<Boolean> = isRegisterSuccessMLD

    private val compositeDisposable = CompositeDisposable()

    var validEmail = false
    var validPhone = false
    var validFirstName = false
    var validSecondName = false
    var validPassword = false

    fun register(
        firstName: String,
        secondName: String,
        email: String,
        phone: String,
        password: String
    ) {
        dataRepository
            .register(
                firstName = firstName,
                secondName = secondName,
                password = password,
                email = email,
                phone = phone
            )
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                        is ResponseEntity.Error -> {
                            when(response.errorMessage) {
                                EMAIL_IS_ALREADY_REGISTERED -> errorMLD.value = EMAIL_IS_ALREADY_REGISTERED
                            }
                            viewStateMLD.value = ViewState.Success()
                        }
                        is ResponseEntity.Success -> {
                            isRegisterSuccessMLD.value = true
                            viewStateMLD.value = ViewState.Success()
                        }
                    }
                },
                onError = { throwable -> errorMLD.value = throwable.message ?: "" }
            ).addTo(compositeDisposable)
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}