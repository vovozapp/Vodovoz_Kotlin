package com.vodovoz.app.feature.questionnaires

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.questionnaires.QuestionnaireResponseJsonParser.parseQuestionnaireResponse
import com.vodovoz.app.data.parser.response.questionnaires.QuestionnaireTypesResponseJsonParser.parseQuestionnaireTypesResponse
import com.vodovoz.app.mapper.QuestionMapper.mapToUI
import com.vodovoz.app.mapper.QuestionnaireTypeMapper.mapToUI
import com.vodovoz.app.ui.model.QuestionUI
import com.vodovoz.app.ui.model.QuestionnaireTypeUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionnairesFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val accountManager: AccountManager,
) : PagingStateViewModel<QuestionnairesFlowViewModel.QuestionnaireState>(QuestionnaireState()) {

    var isTryToGetQuestionnaire = false
    private var lastQuestionnaireType: String? = null

    fun fetchQuestionnaireTypes() {
        viewModelScope.launch {
            uiStateListener.value = state.copy(loadingPage = true)
            flow {
                emit(repository.fetchQuestionnairesResponse())
            }
                .flowOn(Dispatchers.IO)
                .onEach { it ->
                    val response = it.parseQuestionnaireTypesResponse()
                    if (response is ResponseEntity.Success) {
                        response.data.mapToUI().let { list ->
                            uiStateListener.value = state.copy(
                                data = state.data.copy(
                                    questionnaireTypeUIList = list
                                ),
                                loadingPage = false,
                                error = null
                            )
                        }
                    } else {
                        uiStateListener.value =
                            state.copy(
                                loadingPage = false,
                                error = ErrorState.Error()
                            )
                    }
                }.catch {
                    debugLog { "fetch questionnaires types by id error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }


    fun fetchQuestionnaireByType(type: String? = lastQuestionnaireType) {
        lastQuestionnaireType = type
        val userId = accountManager.fetchAccountId()
        viewModelScope.launch {
            uiStateListener.value = state.copy(loadingPage = true)
            flow {
                emit(
                    repository.fetchQuestionnairesResponse(
                        action = lastQuestionnaireType,
                        userId = userId
                    )
                )
            }.flowOn(Dispatchers.Main)
                .onEach {
                    val response = it.parseQuestionnaireResponse()
                    if (response is ResponseEntity.Success) {
                        response.data.mapToUI().let { list ->
                            uiStateListener.value = state.copy(
                                data = state.data.copy(
                                    questionUIList = list
                                ),
                                loadingPage = false,
                                error = null
                            )
                        }
                    } else {
                        uiStateListener.value =
                            state.copy(
                                loadingPage = false,
                                error = ErrorState.Error()
                            )
                    }
                }.catch {
                    debugLog { "fetch questions by id error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    data class QuestionnaireState(
        val questionnaireTypeUIList: List<QuestionnaireTypeUI> = listOf(),
        val questionUIList: List<QuestionUI> = listOf(),
    ) : State
}