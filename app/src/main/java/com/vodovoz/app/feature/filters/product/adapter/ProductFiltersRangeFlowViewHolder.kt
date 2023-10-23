package com.vodovoz.app.feature.filters.product.adapter

import android.content.Context
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderFilterRangeBinding
import com.vodovoz.app.ui.model.FilterUI
import com.vodovoz.app.ui.model.FilterValueUI
import com.vodovoz.app.util.extensions.fromHtml
import java.util.*

class ProductFiltersRangeFlowViewHolder(
    view: View,
    private val onFilterClearClickListener: OnFilterClearClickListener,
    private val onFilterRangeListener: (FilterUI) -> Unit,
) : ItemViewHolder<FilterUI>(view) {

    private val binding: ViewHolderFilterRangeBinding = ViewHolderFilterRangeBinding.bind(view)

    init {

        with(binding) {
            root.setOnClickListener {
                if (!isExpanded) {
                    isExpanded = true
                    llEditTextRange.visibility = View.VISIBLE
                    rsRangeFilter.visibility = View.VISIBLE
                    imgExpand.visibility = View.VISIBLE
                    imgExpand.setImageResource(R.drawable.arrow_up)
                    tvValue.visibility = View.GONE
                    imgClear.visibility = View.GONE
                } else {
                    isExpanded = false
                    llEditTextRange.visibility = View.GONE
                    rsRangeFilter.visibility = View.GONE
                    imgExpand.setImageResource(R.drawable.arrow_down)
                    if (filterUI.filterValueList.isNotEmpty()) {
                        imgExpand.visibility = View.INVISIBLE
                        tvValue.visibility = View.VISIBLE
                        imgClear.visibility = View.VISIBLE
                    }
                }
            }

            rsRangeFilter.addOnChangeListener { slider, _, _ ->
                val range = Pair(slider.values.first(), slider.values.last())
                etMin.setText(String.format(Locale.US, "%.2f", range.first))
                etMax.setText(String.format(Locale.US, "%.2f", range.second))
                fillValues()
            }

            etMin.setOnEditorActionListener { _, actionId, _ ->
                val values = filterUI.values ?: return@setOnEditorActionListener false
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    val minText: String = etMin.text.toString().trim()
                    val minEdit = minText.toFloat()
                    val maxText: String = etMax.text.toString().trim()
                    val maxEdit = maxText.toFloat()
                    if (minText.isNotEmpty()) {
                        if (minEdit < values.min) {
                            etMin.setText(String.format(Locale.US, "%.2f", values.min))
                            rsRangeFilter.values = listOf(values.min, maxEdit)
                        } else if (minEdit > maxEdit) {
                            etMin.setText(String.format(Locale.US, "%.2f", maxEdit))
                            rsRangeFilter.values = listOf(maxEdit, maxEdit)
                        } else {
                            rsRangeFilter.values = listOf(minEdit, maxEdit)
                        }
                    } else {
                        etMin.setText(String.format(Locale.US, "%.2f", values.min))
                        rsRangeFilter.values = listOf(values.min, maxEdit)
                    }
                    fillValues()
                    val imm =
                        etMin.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(etMin.windowToken, 0)
                    etMin.isCursorVisible = false
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            etMax.setOnEditorActionListener { _, actionId, _ ->
                val values = filterUI.values ?: return@setOnEditorActionListener false
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    val minText: String = etMin.text.toString().trim()
                    val minEdit = minText.toFloat()
                    val maxText: String = etMax.text.toString().trim()
                    val maxEdit = maxText.toFloat()
                    if (minText.isNotEmpty()) {
                        if (maxEdit > values.max) {
                            etMax.setText(String.format(Locale.US, "%.2f", values.max))
                            rsRangeFilter.values = listOf(minEdit, values.max)
                        } else if (maxEdit < minEdit) {
                            etMax.setText(String.format(Locale.US, "%.2f", minEdit))
                            rsRangeFilter.values = listOf(minEdit, minEdit)
                        } else {
                            rsRangeFilter.values = listOf(minEdit, maxEdit)
                        }
                    } else {
                        etMax.setText(String.format(Locale.US, "%.2f", values.max))
                        rsRangeFilter.values = listOf(minEdit, values.max)
                    }
                    fillValues()
                    val imm =
                        etMax.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(etMax.windowToken, 0)
                    etMax.isCursorVisible = false
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            etMin.setOnClickListener {
                etMin.isCursorVisible = true
            }

            etMax.setOnClickListener {
                etMax.isCursorVisible = true
            }

            imgClear.setOnClickListener {
                filterUI.filterValueList.clear()
                tvValue.visibility = View.GONE
                imgClear.visibility = View.GONE
                onFilterClearClickListener.onFilterClearClick(filterUI)
            }
        }
    }

    private var isExpanded = false
    private lateinit var filterUI: FilterUI

    override fun bind(item: FilterUI) {
        super.bind(item)
        filterUI = item
        setIsRecyclable(false)
        with(binding) {
            tvName.text = filterUI.name
            tvValue.visibility = View.GONE
            if (isExpanded) {
                llEditTextRange.visibility = View.VISIBLE
                rsRangeFilter.visibility = View.VISIBLE
                imgExpand.visibility = View.VISIBLE
                imgExpand.setImageResource(R.drawable.arrow_up)
                tvValue.visibility = View.GONE
                imgClear.visibility = View.GONE
            } else {
                llEditTextRange.visibility = View.GONE
                rsRangeFilter.visibility = View.GONE
                imgExpand.setImageResource(R.drawable.arrow_down)
                if (filterUI.filterValueList.isNotEmpty()) {
                    imgExpand.visibility = View.INVISIBLE
                    tvValue.visibility = View.VISIBLE
                    imgClear.visibility = View.VISIBLE
                }
            }
            val values = filterUI.values
            if (values != null) {
                etMin.setText(values.min.toString())
                etMax.setText(values.max.toString())
                rsRangeFilter.valueFrom = values.min
                rsRangeFilter.valueTo = values.max
                if (filterUI.filterValueList.isEmpty()) {
                    rsRangeFilter.values = listOf(values.min, values.max)
                    tvValue.text = ""
                } else {
                    rsRangeFilter.values = listOf(
                        filterUI.filterValueList[0].value.toFloat(),
                        filterUI.filterValueList[1].value.toFloat()
                    )
                    val rangeString = StringBuilder("от ")
                        .append(
                            String.format(
                                Locale.US,
                                "<b>%.2f</b>",
                                rsRangeFilter.values.first()
                            )
                        )
                        .append(" до ")
                        .append(String.format(Locale.US, "<b>%.2f<b>", rsRangeFilter.values.last()))
                        .toString()
                    tvValue.text = rangeString.fromHtml()
                }
            }
        }
    }

    private fun fillValues() {
        val range = Pair(
            binding.rsRangeFilter.values.first(),
            binding.rsRangeFilter.values.last()
        )
        if (range.first != filterUI.values?.min || range.second != filterUI.values?.max) {
            filterUI.filterValueList = mutableListOf(
                FilterValueUI(
                    id = filterUI.code.lowercase(Locale.ROOT) + "_from",
                    value = range.first.toString()
                ),
                FilterValueUI(
                    id = filterUI.code.lowercase(Locale.ROOT) + "_to",
                    value = range.second.toString()
                )
            )
            val rangeString = StringBuilder("от ")
                .append(String.format(Locale.US, "<b>%.2f</b>", range.first))
                .append(" до ")
                .append(String.format(Locale.US, "<b>%.2f<b>", range.second))
                .toString()
            binding.tvValue.text = rangeString.fromHtml()
        } else {
            filterUI.filterValueList.clear()
            binding.tvValue.text = ""
        }
        onFilterRangeListener(filterUI)
    }
}