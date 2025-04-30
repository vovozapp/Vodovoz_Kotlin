package com.vodovoz.app.feature.questionnaires.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.ConditionModel
import com.vodovoz.app.domain.general.model.QuestionnairesItemModel
import com.vodovoz.app.domain.general.model.toFieldModel
import com.vodovoz.app.feature.preorder.model.FieldUi
import com.vodovoz.app.feature.preorder.model.toUi

@Immutable
sealed class QuestionnaireComponentUi(
    open val id: String,
    open val error: Boolean,
)

@Immutable
data class FieldComponentUi(
    val ui: FieldUi,
) : QuestionnaireComponentUi(ui.id, ui.isError)

@Immutable
data class SwitchUi(
    override val id: String,
    val label: String,
    val options: List<String>,
    val selectedOption: String,
    val isRequired: Boolean,
    override val error: Boolean = false,
) : QuestionnaireComponentUi(id, error)

@Immutable
data class ConditionsCheckboxListUi(
    override val id: String,
    val label: String,
    val options: List<CheckOption>,
    val conditions: List<ConditionUi>,
    override val error: Boolean = false,
) : QuestionnaireComponentUi(id, error)

@Immutable
data class ConditionUi(
    val id: String,
    val text: String,
    val url: String,
)

@Immutable
data class CheckboxListUi(
    override val id: String,
    val label: String,
    val options: List<CheckOption>,
    val isRequired: Boolean,
    override val error: Boolean = false,
) : QuestionnaireComponentUi(id, error)

@Immutable
data class ToggleListUi(
    override val id: String,
    val label: String,
    val options: List<ToggleOption>,
    val isRequired: Boolean,
    override val error: Boolean = false,
) : QuestionnaireComponentUi(id, error)

@Immutable
data class CheckOption(
    val label: String,
    val isChecked: Boolean,
)

@Immutable
data class ToggleOption(
    val label: String,
    val isSelected: Boolean,
)

fun ConditionModel.toUi(): ConditionUi {
    return ConditionUi(
        id = id,
        text = text,
        url = url
    )
}

fun QuestionnairesItemModel.toUi(): QuestionnaireComponentUi? {
    return when {
        type.lowercase() == "text" -> {
            FieldComponentUi(toFieldModel().toUi())
        }

        conditions.isNotEmpty() -> {
            ConditionsCheckboxListUi(
                id = code,
                label = name,
                options = values.map { CheckOption(it, false) },
                conditions = conditions.map { it.toUi() },
            )
        }

        values.size == 2 && !multiple -> {
            SwitchUi(
                id = code,
                label = name,
                options = values,
                selectedOption = value,
                isRequired = required
            )
        }

        multiple -> {
            CheckboxListUi(
                id = code,
                label = name,
                options = values.map { CheckOption(it, false) },
                isRequired = required
            )
        }

        values.size > 2 && !multiple -> {
            ToggleListUi(
                id = code,
                label = name,
                options = values.map { ToggleOption(it, it == value) },
                isRequired = required
            )
        }

        else -> null
    }
}