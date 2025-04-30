package com.vodovoz.app.feature.questionnaires

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.common.resources.ResourcesProvider
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.design_system.model.VodovozPlaceholderUi
import com.vodovoz.app.design_system.model.mapToUi
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.preorder.model.checkFields
import com.vodovoz.app.feature.preorder.model.getErrorText
import com.vodovoz.app.feature.preorder.model.vodovozValidators
import com.vodovoz.app.feature.questionnaires.model.CheckOption
import com.vodovoz.app.feature.questionnaires.model.CheckboxListUi
import com.vodovoz.app.feature.questionnaires.model.ConditionUi
import com.vodovoz.app.feature.questionnaires.model.ConditionsCheckboxListUi
import com.vodovoz.app.feature.questionnaires.model.FieldComponentUi
import com.vodovoz.app.feature.questionnaires.model.QuestionnaireComponentUi
import com.vodovoz.app.feature.questionnaires.model.SwitchUi
import com.vodovoz.app.feature.questionnaires.model.ToggleListUi
import com.vodovoz.app.feature.questionnaires.model.ToggleOption
import com.vodovoz.app.feature.questionnaires.model.toUi
import com.vodovoz.app.mapper.QuestionnaireMapper.mapToUI
import com.vodovoz.app.ui.model.QuestionUI
import com.vodovoz.app.ui.model.QuestionnaireTypeUI
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.singleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionnairesFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val accountManager: AccountManager,
    private val vodovozServiceRepository: VodovozServiceRepository,
    private val resourcesProvider: ResourcesProvider,
) : PagingContractViewModel<QuestionnairesFlowViewModel.QuestionnaireState, QuestionnairesFlowViewModel.QuestionnaireEvents>(
    QuestionnaireState()
) {

    var isTryToGetQuestionnaire = false
    private var lastQuestionnaireType: String? = null

    init {
        viewModelScope.launch { delay(200) }.invokeOnCompletion {
            fetchWelcomeDetails()
        }
    }


    fun fetchWelcomeDetails() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(uiState = QuestionnairesUiState.Loading, currentWho = null)
        }

        val welcomeDetailsResult =
            vodovozServiceRepository.getQuestionnairesWelcomeDetails().singleResult()

        welcomeDetailsResult.onSuccess { welcomeDetails ->
            uiStateListener.updateData { s ->
                s.copy(
                    uiState = QuestionnairesUiState.Welcome(
                        title = welcomeDetails.title,
                        image = welcomeDetails.image,
                        header = welcomeDetails.header,
                        description = welcomeDetails.description,
                        buttons = welcomeDetails.buttons.mapToUi()
                    )
                )
            }
        }.onFailure {
            uiStateListener.updateData { s ->
                s.copy(uiState = QuestionnairesUiState.Error)
            }
        }
    }

    private fun fetchQuestionnairesDetails() = viewModelScope.launch {
        val currentWho = dataState.currentWho ?: return@launch

        uiStateListener.updateData { s ->
            s.copy(uiState = QuestionnairesUiState.Loading)
        }

        val questionnairesDetailsResult =
            vodovozServiceRepository.getQuestionnairesDetails(currentWho).singleResult()

        questionnairesDetailsResult.onSuccess { questionnairesDetails ->

            uiStateListener.updateData { s ->
                s.copy(
                    button = questionnairesDetails.button.toUi(),
                    components = questionnairesDetails.items.mapNotNull { it.toUi() },
                    title = questionnairesDetails.title,
                    uiState = QuestionnairesUiState.Body
                )
            }

        }.onFailure {
            delay(250)
            fetchWelcomeDetails()
        }
    }

    fun updateText(id: String, newValue: String) = viewModelScope.launch {
        val newComponents = dataState.components.map { comp ->
            if (comp is FieldComponentUi && comp.id == id) {
                comp.copy(
                    ui = comp.ui.copy(
                        value = newValue,
                        isError = false
                    )
                )
            } else comp
        }
        updateComponents(newComponents)
    }

    fun updateSwitch(id: String, option: String) = viewModelScope.launch {
        val newComponents = dataState.components.map { comp ->
            if (comp is SwitchUi && comp.id == id) {
                comp.copy(
                    selectedOption = option,
                    error = false
                )
            } else comp
        }
        updateComponents(newComponents)
    }

    fun updateCheckbox(id: String, option: CheckOption) = viewModelScope.launch {
        val newComponents = dataState.components.map { comp ->
            if (comp is CheckboxListUi && comp.id == id) {
                val updatedOptions = comp.options.map { opt ->
                    if (opt.label == option.label) opt.copy(isChecked = !opt.isChecked)
                    else opt
                }
                comp.copy(options = updatedOptions, error = false)
            } else comp
        }
        updateComponents(newComponents)
    }

    fun updateToggle(id: String, option: ToggleOption) = viewModelScope.launch {
        val newComponents = dataState.components.map { comp ->
            if (comp is ToggleListUi && comp.id == id) {
                val updatedOptions = comp.options.map { opt ->
                    opt.copy(isSelected = (opt.label == option.label))
                }
                comp.copy(options = updatedOptions, error = false)
            } else comp
        }
        updateComponents(newComponents)
    }

    fun updateConditionCheckbox(id: String, option: CheckOption) = viewModelScope.launch {
        val newComponents = dataState.components.map { comp ->
            if (comp is ConditionsCheckboxListUi && comp.id == id) {
                val updatedOptions = comp.options.map { opt ->
                    if (opt.label == option.label) opt.copy(isChecked = !opt.isChecked)
                    else opt
                }
                comp.copy(options = updatedOptions, error = false)
            } else comp
        }
        updateComponents(newComponents)
    }


    private fun updateComponents(newComponents: List<QuestionnaireComponentUi>) {
        uiStateListener.updateData { s ->
            s.copy(components = newComponents)
        }
    }


    fun fetchQuestionnaireTypes() {
        viewModelScope.launch {
            uiStateListener.value = state.copy(loadingPage = true)
            flow {
                emit(repository.fetchQuestionnairesResponse())
            }
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        response.data.mapToUI().let { questionnaire ->
                            uiStateListener.value = state.copy(
                                data = state.data.copy(
                                    message = questionnaire.message,
                                    questionnaireTypeUIList = questionnaire.questionUiTypeList
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
        val userId = accountManager.fetchAccountId() ?: return
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
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        response.data.mapToUI().let { questionnaire ->
                            uiStateListener.value = state.copy(
                                data = state.data.copy(
                                    questionUIList = questionnaire.questionUiList
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

    fun clickBack() {
        if (state.data.questionUIList.isNotEmpty()) {
            uiStateListener.value = state.copy(
                data = state.data.copy(
                    questionUIList = listOf()
                )
            )
        } else {
            uiStateListener.value = state.copy(
                data = state.data.copy(
                    onBack = true
                )
            )
        }
    }

    fun navigateBack() = viewModelScope.launch {
        if (dataState.uiState is QuestionnairesUiState.Body || dataState.currentWho != null) {
            fetchWelcomeDetails()
        } else {
            eventListener.emit(QuestionnaireEvents.GoBack)
        }
    }

    fun activateWelcomeButton(btn: ColorfulButtonUi) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(currentWho = btn.id)
        }
        fetchQuestionnairesDetails()
    }

    fun sendAnswers(button: ColorfulButtonUi) = viewModelScope.launch {
        val checkedComponents = dataState.components.map { component ->
            return@map when {
                component is CheckboxListUi && component.options.none { it.isChecked } -> {
                    component.copy(error = true)
                }

                component is FieldComponentUi && !listOf(component.ui).checkFields(validators = vodovozValidators) -> {
                    val field = component.ui
                    component.copy(
                        ui = field.copy(
                            isError = true,
                            supportingText = field.getErrorText { resourcesProvider.getString(it) }
                        )
                    )
                }

                component is SwitchUi && component.options.find { it == component.selectedOption } == null -> {
                    component.copy(
                        error = true
                    )
                }

                component is ToggleListUi && component.options.none { toggleOption -> toggleOption.isSelected } -> {
                    component.copy(
                        error = true
                    )

                }

                component is ConditionsCheckboxListUi && component.options.any { !it.isChecked } -> {
                    component.copy(
                        error = true
                    )
                }

                else -> {
                    component
                }
            }
        }

        uiStateListener.updateData { s ->
            s.copy(components = checkedComponents)
        }

        val currentWho = dataState.currentWho
        if (checkedComponents.any { component -> component.error } || currentWho == null) {
            eventListener.emit(QuestionnaireEvents.ScrollToTop)
            return@launch
        }

        val answers = dataState.components.toAnswerString()

        val sendQuestionnairesResult =
            vodovozServiceRepository.sendQuestionnairesAnswers(currentWho, answers).singleResult()

        sendQuestionnairesResult.onSuccess {
            it
        }.onFailure {
            it
        }
    }

    private fun List<QuestionnaireComponentUi>.toAnswerString(): String {
        return this
            .mapNotNull { comp ->
                val raw = when (comp) {
                    is FieldComponentUi         -> comp.ui.value
                    is SwitchUi                 -> comp.selectedOption
                    is CheckboxListUi           -> comp.options
                        .filter { it.isChecked }
                        .joinToString(",") { it.label }
                    is ToggleListUi             -> comp.options
                        .firstOrNull { it.isSelected }
                        ?.label
                        .orEmpty()
                    is ConditionsCheckboxListUi -> comp.options
                        .filter { it.isChecked }
                        .joinToString(",") { it.label }
                }
                if (raw.isBlank()) return@mapNotNull null

                val cleaned = raw
                    .removePrefix("[")
                    .removeSuffix("]")
                    .replace(", ", ",")
                    .trim()

                "${comp.id}\$$cleaned"
            }
            .joinToString(separator = ";", postfix = ";")
    }

    fun navigateToWebView(condition: ConditionUi) = viewModelScope.launch {
        eventListener.emit(QuestionnaireEvents.GoToWebView(condition.url))
    }


    @Immutable
    data class QuestionnaireState(
        val message: String = "",
        val questionnaireTypeUIList: List<QuestionnaireTypeUI> = listOf(),
        val questionUIList: List<QuestionUI> = listOf(),
        val onBack: Boolean = false,

        val uiState: QuestionnairesUiState = QuestionnairesUiState.Loading,
        val currentWho: String? = null,
        val title: String = "",
        val button: ColorfulButtonUi = ColorfulButtonUi.Empty,
        val components: List<QuestionnaireComponentUi> = emptyList(),
    ) : State

    sealed class QuestionnaireEvents : Event {
        data class GoToWebView(val url: String) : QuestionnaireEvents()
        data object GoBack : QuestionnaireEvents()
        data object ScrollToTop : QuestionnaireEvents()
    }

    @Immutable
    sealed interface QuestionnairesUiState {

        data object Loading : QuestionnairesUiState
        data class Welcome(
            val title: String,
            val image: String,
            val header: String,
            val description: String,
            val buttons: List<ColorfulButtonUi>,
        ) : QuestionnairesUiState

        data object Body : QuestionnairesUiState
        data class Success(val placeholder: VodovozPlaceholderUi) : QuestionnairesUiState

        data object Error : QuestionnairesUiState

    }
}