package com.vodovoz.app.ui.fragment.questionnaires

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.QuestionMapper.mapToUI
import com.vodovoz.app.mapper.QuestionnaireTypeMapper.mapToUI
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.model.QuestionUI
import com.vodovoz.app.ui.model.QuestionnaireTypeUI
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class QuestionnairesViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val questionnaireTypeListMLD = MutableLiveData<List<QuestionnaireTypeUI>>()
    private val questionUIListMLD = MutableLiveData<List<QuestionUI>>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val questionnaireTypeListLD: LiveData<List<QuestionnaireTypeUI>> = questionnaireTypeListMLD
    val questionUIListLD: LiveData<List<QuestionUI>> = questionUIListMLD

    private val compositeDisposable = CompositeDisposable()

    var isTryToGetQuestionnaire = false
    private var lastQuestionnaireType: String? = null

    fun fetchQuestionnaireTypes() {
        dataRepository
            .fetchQuestionnairesTypes()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Error("Неизвестная ошибка")
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                        is ResponseEntity.Success -> {
                            questionnaireTypeListMLD.value = response.data.mapToUI()
                            viewStateMLD.value = ViewState.Success()
                        }
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message ?: "Неизвестная ошибка") }
            ).addTo(compositeDisposable)
    }


    fun fetchQuestionnaireByType(type: String? = lastQuestionnaireType) {
        lastQuestionnaireType = type
        dataRepository
            .fetchQuestionnaire(lastQuestionnaireType)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                isTryToGetQuestionnaire = true
                viewStateMLD.value = ViewState.Loading()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Error("Неизвестаня ошибка")
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                        is ResponseEntity.Success -> {
                            questionUIListMLD.value = response.data.mapToUI()
                            viewStateMLD.value = ViewState.Success()
                        }
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message ?: "Неизвестная ошибка") }
            ).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}