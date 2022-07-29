package com.vodovoz.app.ui.fragment.contacts

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.ContactsBundleMapper.mapToUI
import com.vodovoz.app.ui.base.BaseViewModel
import com.vodovoz.app.ui.model.ContactsBundleUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class ContactsViewModel(
    private val dataRepository: DataRepository
) : BaseViewModel() {

    private val contactBundleUIMLD = MutableLiveData<ContactsBundleUI>()
    private val messageMLD = MutableLiveData<String>()

    val contactBundleUILD: LiveData<ContactsBundleUI> = contactBundleUIMLD
    val messageLD: LiveData<String> = messageMLD

    fun fetchContacts() {
        dataRepository
            .fetchContacts(Build.VERSION.RELEASE)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { stateLoading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Error -> stateError(response.errorMessage)
                        is ResponseEntity.Hide -> stateError()
                        is ResponseEntity.Success -> {
                            contactBundleUIMLD.value = response.data.mapToUI()
                            stateSuccess()
                        }
                    }
                },
                onError = { stateError(it.message) }
            ).addTo(compositeDisposable)
    }

    fun sendMail(
        name: String,
        phone: String,
        email: String,
        descriptions: String
    ) {
        dataRepository
            .sendMail(
                name = name,
                phone = phone,
                email = email,
                comment = descriptions
            )
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { stateLoading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    stateSuccess()
                     when  (response) {
                         is ResponseEntity.Success -> messageMLD.value = "Ваще обращение успешно отправлено"
                         is ResponseEntity.Error -> messageMLD.value = response.errorMessage
                         is ResponseEntity.Hide -> messageMLD.value = "Неизвестная ошибка"
                     }
                },
                onError = {  stateSuccess()
                    messageMLD.value = it.message ?: "Неизвестная ошибка"
                }
            ).addTo(compositeDisposable)
    }

}