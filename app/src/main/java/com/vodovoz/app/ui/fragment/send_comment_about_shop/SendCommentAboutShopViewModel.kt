package com.vodovoz.app.ui.fragment.send_comment_about_shop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.util.FieldValidationsSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class SendCommentAboutShopViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val errorMLD = MutableLiveData<String>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val errorLD: LiveData<String> = errorMLD

    private var productId: Long? = null

    fun updateArgs(productId: Long) {
        this.productId = productId
    }

    fun validate(
        comment: String,
        rating: Int
    ) {
        if (rating == 0) {
            errorMLD.value = "Поставьте оценку от 1 до 5"
            return
        }

        if (comment.length < FieldValidationsSettings.MIN_COMMENT_LENGTH) {
            errorMLD.value = "Длина отзыва должа быть не менее ${FieldValidationsSettings.MIN_COMMENT_LENGTH} символов"
            return
        }

        sendComment(
            comment = comment,
            rating = rating
        )
    }

    private fun sendComment(
        comment: String,
        rating: Int
    ) {
        dataRepository
            .sendCommentAboutShop(
                comment = comment,
                rating = rating
            )
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                     when(response) {
                         is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                         is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                         is ResponseEntity.Success -> viewStateMLD.value = ViewState.Success()
                     }
                },
                onError = { throwable ->
                    viewStateMLD.value = ViewState.Error(throwable.message ?: "Неизвестаня ошибка")
                }
            )

    }

}