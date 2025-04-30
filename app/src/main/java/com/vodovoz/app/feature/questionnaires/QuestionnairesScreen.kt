package com.vodovoz.app.feature.questionnaires

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.feature.questionnaires.components.QuestionnairesBody

@Suppress("NonSkippableComposable")
@Composable
fun QuestionnairesScreen(
    viewModel: QuestionnairesFlowViewModel,
    viewState: QuestionnairesFlowViewModel.QuestionnaireState,
    scrollState: ScrollState,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .fillMaxSize()
    ) {
        VodovozTopBar(
            onBack = { viewModel.navigateBack() },
            title = viewState.title
        )
        QuestionnairesBody(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            components = viewState.components,
            button = viewState.button,
            onButtonClick = { button ->
                viewModel.sendAnswers(button)
            },
            onFieldChange = { field, newValue ->
                viewModel.updateText(field.id, newValue.value)
            },
            onSwitchChange = { switch, option ->
                viewModel.updateSwitch(switch.id, option)
            },
            onToggleChange = { toggle, option ->
                viewModel.updateToggle(toggle.id, option)
            },
            onCheckboxChange = { list, option ->
                viewModel.updateCheckbox(list.id, option)
            },
            onConditionCheckboxChange = { conditionList, option ->
                viewModel.updateConditionCheckbox(conditionList.id, option)
            },
            onConditionClick = { condition ->
                viewModel.navigateToWebView(condition)
            }
        )
    }
}