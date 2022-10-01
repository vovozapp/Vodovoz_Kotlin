package com.vodovoz.app.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.*
import com.vodovoz.app.ui.adapter.ViewType.Companion.viewTypeByValue
import com.vodovoz.app.util.LogSettings

class FormAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var afterErrorChange: (FormField) -> Unit
    private lateinit var onPromptClick: (FormField) -> Unit
    private lateinit var onFieldClick: (FormField, Int?) -> Unit
    private lateinit var onSwitchChange: (FormField) -> Unit

    var formFieldList = listOf<FormField>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun setupListeners(
        afterErrorChange: (FormField) -> Unit,
        onPromptClick: (FormField) -> Unit,
        onFieldClick: (FormField, Int?) -> Unit,
        onSwitchChange: (FormField) -> Unit
    ) {
        this.afterErrorChange = afterErrorChange
        this.onPromptClick = onPromptClick
        this.onFieldClick = onFieldClick
        this.onSwitchChange = onSwitchChange
    }

    override fun getItemViewType(position: Int) = formFieldList[position].viewType.value

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = when(viewType.viewTypeByValue()) {
        ViewType.SINGLE_LINE -> SingleLineFieldVH(
            ViewHolderFieldSingleLineBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            afterErrorChange,
            onFieldClick
        )
        ViewType.DOUBLE_LINE -> DoubleLineFieldVH(
            ViewHolderFieldDoubleLineBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            afterErrorChange,
            onFieldClick
        )
        ViewType.TITLE -> TitleFieldVH(
            ViewHolderFieldTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        ViewType.SINGLE_LINE_WITH_PROMPT -> SingleLineWithPromptFieldVH(
            ViewHolderFieldSingleLineWithPromptBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            afterErrorChange,
            onPromptClick,
            onFieldClick
        )
        ViewType.SWITCH -> SwitchFieldVH(
            ViewHolderFieldSwitchBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onSwitchChange
        )
        ViewType.VALUE -> ValueFieldVH(
            ViewHolderFieldValueBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onFieldClick
        )
        ViewType.PRICE -> PriceFieldVH(
            ViewHolderFieldPriceBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            parent.context
        )
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int
    ) = when(getItemViewType(position).viewTypeByValue()) {
        ViewType.SINGLE_LINE -> (holder as SingleLineFieldVH).onBind(formFieldList[position] as FormField.SingleLineField)
        ViewType.DOUBLE_LINE -> (holder as DoubleLineFieldVH).onBind(formFieldList[position] as FormField.DoubleLineField)
        ViewType.TITLE -> (holder as TitleFieldVH).onBind(formFieldList[position] as FormField.TitleField)
        ViewType.SINGLE_LINE_WITH_PROMPT -> (holder as SingleLineWithPromptFieldVH).onBind(formFieldList[position] as FormField.SingleLineWithPromptField)
        ViewType.SWITCH -> (holder as SwitchFieldVH).onBind(formFieldList[position] as FormField.SwitchField)
        ViewType.VALUE -> (holder as ValueFieldVH).onBind(formFieldList[position] as FormField.ValueField)
        ViewType.PRICE -> (holder as PriceFieldVH).onBind(formFieldList[position] as FormField.PriceField)
    }

    override fun getItemCount() = formFieldList.size

}

private class SingleLineWithPromptFieldVH(
    private val binding: ViewHolderFieldSingleLineWithPromptBinding,
    private val afterErrorChange: (FormField) -> Unit,
    private val onPromptClick: (FormField) -> Unit,
    private val onFieldClick: (FormField, Int?) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var field: FormField.SingleLineWithPromptField

    fun onBind(field: FormField.SingleLineWithPromptField) {
        this.field = field
        when(field.isValid) {
            false -> binding.tvName.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
            true -> binding.tvName.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_black))
        }
        when(field.isEditable) {
            true -> binding.tvValue.onFocusChangeListener = null
            false -> {
                binding.tvValue.onFocusChangeListener = View.OnFocusChangeListener { p0, p1 ->
                    binding.tvValue.clearFocus()
                    if (p1) onFieldClick(field, null)
                }
            }
        }
        binding.tvPrompt.setOnClickListener { onPromptClick(field) }
        binding.tvName.text = field.name
        binding.tvPrompt.text = field.prompt
        binding.tvValue.setText(field.value)
        binding.tvValue.hint = field.hint
    }

}

private class SingleLineFieldVH(
    private val binding: ViewHolderFieldSingleLineBinding,
    private val afterErrorChange: (FormField) -> Unit,
    private val onFieldClick: (FormField, Int?) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.tvValue.onFocusChangeListener = View.OnFocusChangeListener { _, isFocused ->
            when(field.isEditable) {
                true -> { Log.d(LogSettings.DEVELOP_LOG, "${field.fieldType.name}, true") }
                false -> {
                    Log.d(LogSettings.DEVELOP_LOG, "${field.fieldType.name}, false")
                    binding.tvValue.clearFocus()
                    if (isFocused) onFieldClick(field, null)
                }
            }
        }
        binding.tvValue.doAfterTextChanged {
            field.value = it.toString()
        }
    }

    private lateinit var field: FormField.SingleLineField

    fun onBind(field: FormField.SingleLineField) {
        this.field = field
        when(field.isValid) {
            false -> binding.tvName.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
            true -> binding.tvName.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_black))
        }
        if (binding.tvValue.text.toString() != field.value) binding.tvValue.setText(field.value)
        binding.tvName.text = field.name
        binding.tvValue.hint = field.hint
    }

}

private class ValueFieldVH(
    private val binding: ViewHolderFieldValueBinding,
    private val onFieldClick: (FormField, Int?) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var field: FormField.ValueField

    fun onBind(field: FormField.ValueField) {
        this.field = field
        binding.tvValue.doAfterTextChanged { field.value = it.toString() }
        binding.tvValue.setText(field.value)
        binding.tvValue.hint = field.hint
        when(field.isEditable) {
            true -> binding.tvValue.onFocusChangeListener = null
            false -> {
                binding.tvValue.onFocusChangeListener = View.OnFocusChangeListener { p0, p1 ->
                    binding.tvValue.clearFocus()
                    if (p1) onFieldClick(field, null)
                }
            }
        }
    }

}

private class DoubleLineFieldVH(
    private val binding: ViewHolderFieldDoubleLineBinding,
    private val afterErrorChange: (FormField) -> Unit,
    private val onFieldClick: (FormField, Int?) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var field: FormField.DoubleLineField

    fun onBind(field: FormField.DoubleLineField) {
        this.field = field
        binding.etFirstValue.doAfterTextChanged {
            field.firstValue = it.toString()
            if (!field.isValid) afterErrorChange(field)
        }
        binding.etSecondValue.doAfterTextChanged {
            field.secondValue = it.toString()
            if (!field.isValid) afterErrorChange(field)
        }
        binding.tvName.text = field.name
        if (binding.etFirstValue.text.toString() != field.firstValue) {
            binding.etFirstValue.setText(field.firstValue)
            Log.d(LogSettings.DEVELOP_LOG, "FIRST VALUE IF")
        } else {
            Log.d(LogSettings.DEVELOP_LOG, "FIRST VALUE ELSE")
        }
        if (binding.etSecondValue.text.toString() != field.secondValue) {
            binding.etSecondValue.setText(field.secondValue)
            Log.d(LogSettings.DEVELOP_LOG, "SECOND VALUE IF")
        } else {
            Log.d(LogSettings.DEVELOP_LOG, "SECOND VALUE ELSE")
        }
        if (binding.etSecondValue.hint?.toString() != field.secondHint) {
            binding.etSecondValue.hint = field.secondHint
            Log.d(LogSettings.DEVELOP_LOG, "SECOND HINT IF")
        } else {
            Log.d(LogSettings.DEVELOP_LOG, "SECOND HINT ELSE")
        }
        if (binding.etFirstValue.hint?.toString() != field.firstHint) {
            binding.etFirstValue.hint = field.firstHint
            Log.d(LogSettings.DEVELOP_LOG, "SECOND HINT IF")
        } else {
            Log.d(LogSettings.DEVELOP_LOG, "SECOND HINT ELSE")
        }
        when(field.isValid) {
            false -> binding.tvName.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
            true -> binding.tvName.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_black))
        }
        when(field.isEditableFirst) {
            true -> binding.etFirstValue.onFocusChangeListener = null
            false -> {
                binding.etFirstValue.onFocusChangeListener = View.OnFocusChangeListener { p0, p1 ->
                    binding.etFirstValue.clearFocus()
                    if (p1) onFieldClick(field, 1)
                }
            }
        }
        when(field.isEditableSecond) {
            true -> binding.etSecondValue.setOnEditorActionListener(null)
            false -> {
                binding.etSecondValue.onFocusChangeListener = View.OnFocusChangeListener { p0, p1 ->
                    binding.etSecondValue.clearFocus()
                    if (p1) onFieldClick(field, 2)
                }
            }
        }
    }

}

private class SwitchFieldVH(
    private val binding: ViewHolderFieldSwitchBinding,
    private val onSwitchChange: (FormField) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var field: FormField.SwitchField

    fun onBind(field: FormField.SwitchField) {
        this.field = field
        binding.tvValue.setOnCheckedChangeListener { _, checked ->
            field.value = checked
            onSwitchChange(field)
        }
        binding.tvValue.text = field.name
        if (binding.tvValue.isChecked != field.value) binding.tvValue.isChecked = field.value
    }

}

private class TitleFieldVH(
    private val binding: ViewHolderFieldTitleBinding
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var field: FormField.TitleField

    fun onBind(field: FormField.TitleField) {
        this.field = field
        binding.tvName.text = field.name
    }

}

private class PriceFieldVH(
    private val binding: ViewHolderFieldPriceBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var field: FormField.PriceField

    fun onBind(field: FormField.PriceField) {
        this.field = field
        binding.tvName.text = field.name
        binding.tvValue.text = String.format(context.getString(R.string.price_text), field.value)
    }

}


enum class FieldType {
    ADDRESS, NAME, COMPANY_NAME, EMAIL, PHONE, DATE, PAY_METHOD, INPUT_CASH,
    OPERATOR_CALL, ALERT_DRIVER, COMMENT, DELIVERY_PRICE, DISCOUNT,
    DEPOSIT, PRODUCTS_PRICE, TOTAL, TITLE
}

enum class ViewType(val value: Int) {
    SINGLE_LINE(1), DOUBLE_LINE(2), TITLE(3),
    SWITCH(4), SINGLE_LINE_WITH_PROMPT(5), VALUE(6),
    PRICE(7);

    companion object {
        fun Int.viewTypeByValue() = when(this) {
            1 -> SINGLE_LINE
            2 -> DOUBLE_LINE
            3 -> TITLE
            4 -> SWITCH
            5 -> SINGLE_LINE_WITH_PROMPT
            6 -> VALUE
            7 -> PRICE
            else -> throw Exception("Unknown view type value")
        }
    }
}

sealed class FormField(
    val fieldType: FieldType,
    val viewType: ViewType,
    var isValid: Boolean = true
) {
    class SingleLineWithPromptField(
        type: FieldType,
        val name: String,
        var value: String = "",
        val hint: String,
        val prompt: String,
        val isEditable: Boolean
    ) : FormField(type, ViewType.SINGLE_LINE_WITH_PROMPT)

    class SingleLineField(
        type: FieldType,
        val name: String,
        var value: String = "",
        val hint: String,
        val isEditable: Boolean
    ) : FormField(type, ViewType.SINGLE_LINE)

    class DoubleLineField(
        type: FieldType,
        val name: String,
        var firstValue: String = "",
        var secondValue: String = "",
        val firstHint: String,
        val secondHint: String,
        val isEditableFirst: Boolean,
        val isEditableSecond: Boolean
    ) : FormField(type, ViewType.DOUBLE_LINE)

    class SwitchField(
        type: FieldType,
        val name: String,
        var value: Boolean = false
    ) : FormField(type, ViewType.SWITCH)

    class TitleField(
        type: FieldType,
        val name: String
    ) : FormField(type, ViewType.TITLE)

    class ValueField(
        type: FieldType,
        var value: String = "",
        val hint: String,
        val isEditable: Boolean
    ) : FormField(type, ViewType.VALUE)

    class PriceField(
        type: FieldType,
        val name: String,
        var value: Int
    ) : FormField(type, ViewType.PRICE)

}