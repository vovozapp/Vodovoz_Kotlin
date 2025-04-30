package com.vodovoz.app.feature.questionnaires.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vodovoz.app.design_system.composables.button.VodovozButtonsColumn
import com.vodovoz.app.design_system.composables.button.VodovozRadioButton
import com.vodovoz.app.design_system.composables.decoration.VodovozHorizontalDivider
import com.vodovoz.app.design_system.composables.text_fields.VodovozTextField
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.feature.preorder.model.FieldUi
import com.vodovoz.app.feature.questionnaires.model.CheckOption
import com.vodovoz.app.feature.questionnaires.model.CheckboxListUi
import com.vodovoz.app.feature.questionnaires.model.ConditionUi
import com.vodovoz.app.feature.questionnaires.model.ConditionsCheckboxListUi
import com.vodovoz.app.feature.questionnaires.model.FieldComponentUi
import com.vodovoz.app.feature.questionnaires.model.QuestionnaireComponentUi
import com.vodovoz.app.feature.questionnaires.model.SwitchUi
import com.vodovoz.app.feature.questionnaires.model.ToggleListUi
import com.vodovoz.app.feature.questionnaires.model.ToggleOption

@Suppress("NonSkippableComposable")
@Composable
fun QuestionnairesBody(
    modifier: Modifier = Modifier,
    components: List<QuestionnaireComponentUi>,
    button: ColorfulButtonUi,
    onButtonClick: (ColorfulButtonUi) -> Unit,
    onCheckboxChange: (CheckboxListUi, CheckOption) -> Unit,
    onConditionCheckboxChange: (ConditionsCheckboxListUi, CheckOption) -> Unit,
    onConditionClick: (ConditionUi) -> Unit,
    onFieldChange: (FieldComponentUi, FieldUi) -> Unit,
    onToggleChange: (ToggleListUi, ToggleOption) -> Unit,
    onSwitchChange: (SwitchUi, String) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            components.forEachIndexed { index, component ->
                key(component.id) {
                    if (component !is FieldComponentUi && index != components.lastIndex) {
                        VodovozHorizontalDivider()
                    }

                    when (component) {
                        is CheckboxListUi -> {
                            CheckboxListComponent(
                                ui = component,
                                onClick = onCheckboxChange
                            )
                        }

                        is FieldComponentUi -> {
                            VodovozTextField(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                field = component.ui,
                                onFieldChange = { _, updatedField ->
                                    onFieldChange(component, updatedField)
                                }
                            )
                        }

                        is SwitchUi -> {
                            SwitchComponent(
                                ui = component,
                                onClick = onSwitchChange
                            )
                        }

                        is ToggleListUi -> {
                            ToggleListComponent(
                                ui = component,
                                onClick = onToggleChange
                            )
                        }

                        is ConditionsCheckboxListUi -> {
                            ConditionCheckboxListComponent(
                                ui = component,
                                onClick = onConditionCheckboxChange,
                                onConditionClick = onConditionClick
                            )
                        }
                    }
                }
            }
        }

        VodovozButtonsColumn(
            buttons = listOf(button),
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 24.dp),
            onButtonClick = onButtonClick
        )
    }
}

@Composable
private fun ConditionCheckboxListComponent(
    modifier: Modifier = Modifier,
    ui: ConditionsCheckboxListUi,
    onClick: (ConditionsCheckboxListUi, CheckOption) -> Unit,
    onConditionClick: (ConditionUi) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = ui.label,
            color = if(ui.error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint,
            style = MaterialTheme.typography.bodySmall
        )
        ui.conditions.forEach { condition ->
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable { onConditionClick(condition) },
                text = condition.text,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium.copy(letterSpacing = 0.sp)
            )
        }

        Column {

            ui.options.forEach { option ->
                Row(modifier = Modifier
                    .clickable { onClick(ui, option) }
                    .padding(16.dp)) {
                    Checkbox(
                        modifier = Modifier.size(24.dp),
                        checked = option.isChecked,
                        onCheckedChange = {
                            onClick(ui, option)
                        },
                        colors = CheckboxDefaults.colors(
                            checkmarkColor = MaterialTheme.colorScheme.background,
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Text(
                        text = option.label,
                        modifier = Modifier.padding(start = 16.dp).weight(1f),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium
                    )

                }
            }
        }
    }
}

@Composable
private fun SwitchComponent(
    modifier: Modifier = Modifier, ui: SwitchUi,
    onClick: (SwitchUi, String) -> Unit,
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            text = ui.label,
            color = if (ui.error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall
        )
        FlowRow(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ui.options.forEach { opt ->
                Button(
                    modifier = Modifier.requiredHeight(38.dp),
                    onClick = { onClick(ui, opt) },
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (ui.selectedOption == opt) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = opt,
                        color = if (ui.selectedOption == opt) {
                            MaterialTheme.colorScheme.background
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(letterSpacing = 0.1.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun CheckboxListComponent(
    modifier: Modifier = Modifier,
    ui: CheckboxListUi,
    onClick: (CheckboxListUi, CheckOption) -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            text = ui.label,
            color = if (ui.error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall
        )
        ui.options.forEach { opt ->
            Row(
                modifier = Modifier
                    .clickable {
                        onClick(ui, opt)
                    }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = opt.label,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium
                )

                Checkbox(
                    modifier = Modifier.size(24.dp),
                    checked = opt.isChecked,
                    onCheckedChange = {
                        onClick(ui, opt)
                    },
                    colors = CheckboxDefaults.colors(
                        checkmarkColor = MaterialTheme.colorScheme.background,
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.outline
                    )
                )

            }
        }
    }
}

@Composable
private fun ToggleListComponent(
    modifier: Modifier = Modifier,
    ui: ToggleListUi,
    onClick: (ToggleListUi, ToggleOption) -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            text = ui.label,
            color = if (ui.error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall
        )
        ui.options.forEach { opt ->
            Row(
                modifier = Modifier
                    .clickable {
                        onClick(ui, opt)
                    }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = opt.label,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium
                )

                VodovozRadioButton(
                    modifier = Modifier.padding(start = 16.dp),
                    selected = opt.isSelected,
                    onClick = {
                        onClick(ui, opt)
                    }
                )
            }
        }
    }
}