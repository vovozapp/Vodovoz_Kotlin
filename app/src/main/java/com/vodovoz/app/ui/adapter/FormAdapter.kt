package com.vodovoz.app.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.*
import com.vodovoz.app.ui.adapter.ViewType.Companion.viewTypeByValue
import java.lang.Exception

class FormAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var afterErrorChange: (FormField) -> Unit
    private lateinit var onPromptClick: (FormField) -> Unit
    private lateinit var onFieldClick: (FormField) -> Unit

    var formFieldList = listOf<FormField>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun setupListeners(
        afterErrorChange: (FormField) -> Unit,
        onPromptClick: (FormField) -> Unit
    ) {
        this.afterErrorChange = afterErrorChange
        this.onPromptClick = onPromptClick
    }

    override fun getItemViewType(position: Int) = formFieldList[position].viewType.value

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = when(viewType.viewTypeByValue()) {
        ViewType.SINGLE_LINE -> SingleLineFieldVH(
            ViewHolderSingleLineFieldBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            afterErrorChange,
            onFieldClick
        )
        ViewType.DOUBLE_LINE -> DoubleLineFieldVH(
            ViewHolderDoubleLineFieldBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            afterErrorChange,
            onFieldClick
        )
        ViewType.TITLE -> TitleFieldVH(
            ViewHolderTitleFieldBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        ViewType.SINGLE_LINE_WITH_PROMPT -> SingleLineWithPromptFieldVH(
            ViewHolderSingleLineWithPromptFieldBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            afterErrorChange,
            onPromptClick,
            onFieldClick
        )
        ViewType.SWITCH -> SwitchFieldVH(
            ViewHolderSwitchFieldBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        ViewType.VALUE -> ValueFieldVH(
            ViewHolderValueFieldBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        ViewType.PRICE -> PriceFieldVH(
            ViewHolderPriceFieldBinding.inflate(LayoutInflater.from(parent.context), parent, false),
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
    private val binding: ViewHolderSingleLineWithPromptFieldBinding,
    private val afterErrorChange: (FormField) -> Unit,
    private val onPromptClick: (FormField) -> Unit,
    private val onFieldClick: (FormField) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.value.doAfterTextChanged {
            field.value = it.toString()
            field.error?.let { afterErrorChange(field) }
        }
        binding.prompt.setOnClickListener { onPromptClick(field) }
    }

    private lateinit var field: FormField.SingleLineWithPromptField

    fun onBind(field: FormField.SingleLineWithPromptField) {
        this.field = field
        binding.name.text = field.name
        binding.prompt.text = field.prompt
        binding.value.setText(field.value)
        binding.value.hint = field.hint
    }

}

private class SingleLineFieldVH(
    private val binding: ViewHolderSingleLineFieldBinding,
    private val afterErrorChange: (FormField) -> Unit,
    private val onFieldClick: (FormField) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.value.doAfterTextChanged {
            field.value = it.toString()
            field.error?.let { afterErrorChange(field) }
        }
    }

    private lateinit var field: FormField.SingleLineField

    fun onBind(field: FormField.SingleLineField) {
        this.field = field
        binding.name.text = field.name
        binding.value.setText(field.value)
        binding.value.hint = field.hint
    }

}

private class ValueFieldVH(
    private val binding: ViewHolderValueFieldBinding
) : RecyclerView.ViewHolder(binding.root) {

    init { binding.value.doAfterTextChanged { field.value = it.toString() } }

    private lateinit var field: FormField.ValueField

    fun onBind(field: FormField.ValueField) {
        this.field = field
        binding.value.setText(field.value)
        binding.value.hint = field.hint
    }

}

private class DoubleLineFieldVH(
    private val binding: ViewHolderDoubleLineFieldBinding,
    private val afterErrorChange: (FormField) -> Unit,
    private val onFieldClick: (FormField) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.firstValue.doAfterTextChanged {
            field.firstValue = it.toString()
            field.error?.let { afterErrorChange(field) }
        }
        binding.secondValue.doAfterTextChanged {
            field.secondValue = it.toString()
            field.error?.let { afterErrorChange(field) }
        }
    }

    private lateinit var field: FormField.DoubleLineField

    fun onBind(field: FormField.DoubleLineField) {
        this.field = field
        binding.name.text = field.name
        binding.firstValue.setText(field.firstValue)
        binding.firstValue.setText(field.secondValue)
        binding.firstValue.hint = field.firstHint
        binding.secondValue.hint = field.secondHint
    }

}

private class SwitchFieldVH(
    private val binding: ViewHolderSwitchFieldBinding
) : RecyclerView.ViewHolder(binding.root) {

    init { binding.value.setOnCheckedChangeListener { _, checked -> field.value = checked } }

    private lateinit var field: FormField.SwitchField

    fun onBind(field: FormField.SwitchField) {
        this.field = field
        binding.value.text = field.name
        binding.value.isChecked = field.value
    }

}

private class TitleFieldVH(
    private val binding: ViewHolderTitleFieldBinding
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var field: FormField.TitleField

    fun onBind(field: FormField.TitleField) {
        this.field = field
        binding.name.text = field.name
    }

}

private class PriceFieldVH(
    private val binding: ViewHolderPriceFieldBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var field: FormField.PriceField

    fun onBind(field: FormField.PriceField) {
        this.field = field
        binding.name.text = field.name
        binding.value.text = String.format(context.getString(R.string.price_text), field.value)
    }

}


enum class FieldType {
    ADDRESS, NAME, COMPANY_NAME, EMAIL, PHONE, DATE, PAY_METHOD,
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
    val error: String? = null
) {
    class SingleLineWithPromptField(
        type: FieldType,
        val name: String,
        var value: String = "",
        val hint: String,
        val prompt: String
    ) : FormField(type, ViewType.SINGLE_LINE_WITH_PROMPT)

    class SingleLineField(
        type: FieldType,
        val name: String,
        var value: String = "",
        val hint: String
    ) : FormField(type, ViewType.SINGLE_LINE)

    class DoubleLineField(
        type: FieldType,
        val name: String,
        var firstValue: String = "",
        var secondValue: String = "",
        val firstHint: String,
        val secondHint: String
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
        val hint: String
    ) : FormField(type, ViewType.VALUE)

    class PriceField(
        type: FieldType,
        val name: String,
        var value: Int
    ) : FormField(type, ViewType.PRICE)

}