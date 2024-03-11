package com.vodovoz.app.feature.buy_certificate.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.chip.Chip
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderChooseCertificateBinding
import com.vodovoz.app.databinding.ViewSimpleTextChipBigBinding
import com.vodovoz.app.ui.model.custom.BuyCertificateFieldUI
import com.vodovoz.app.ui.model.custom.BuyCertificatePropertyUI
import com.vodovoz.app.util.extensions.debugLog

class ChooseCertificateViewHolder(
    private val binding: ViewHolderChooseCertificateBinding,
    private val onChangeAmount: (String) -> Unit,
    private val onChange: (String, BuyCertificateFieldUI) -> Unit,
) : ItemViewHolder<BuyCertificatePropertyUI>(binding.root) {

    init {
        binding.amount.doAfterTextChanged {
            onChangeAmount(it.toString())
        }

        binding.increaseAmount.setOnClickListener {
            binding.amount.text = (binding.amount.text.toString().toInt() + 1).toString()
        }

        binding.reduceAmount.setOnClickListener {
            val currentAmount = binding.amount.text.toString().toInt()
            if (currentAmount > 1) {
                binding.amount.text = (currentAmount - 1).toString()
            }
        }
    }

    override fun bind(item: BuyCertificatePropertyUI) {
        super.bind(item)
        debugLog { "ChooseCertificateViewHolder onBind" }
        var addStar = ""
        if (item.required) {
            addStar = "*"
        }
        binding.name.text = buildString {
            append(item.name)
            append(addStar)
        }
        val color = if (item.error) {
            R.color.red
        } else {
            R.color.blackTextDark
        }
        binding.name.setTextColor(
            ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, color))
        )

        binding.chipGroupCertificate.removeAllViews()
        item.buyCertificateFieldUIList?.forEach {
            binding.chipGroupCertificate.addView(
                buildQueryChip(item.code, it, item.currentValue)
            )
        }

        if (item.showAmount) {
            binding.amountControllerDeployed.visibility = View.VISIBLE
        }

        if (item.text.isNotEmpty()) {
            binding.txtViewText.text = item.text
        } else {
            binding.txtViewText.visibility = View.GONE
        }
    }


    private fun buildQueryChip(
        itemCode: String,
        certificateField: BuyCertificateFieldUI,
        currentValue: String,
    ): Chip {
        val chip =
            ViewSimpleTextChipBigBinding.inflate(LayoutInflater.from(binding.root.context)).root
        chip.text = certificateField.name
        chip.chipBackgroundColor = if (certificateField.id == currentValue) {
            ColorStateList.valueOf(ContextCompat.getColor(chip.context, R.color.bluePrimary))
        } else {
            ColorStateList.valueOf(ContextCompat.getColor(chip.context, R.color.gray))
        }
        chip.setTextColor(
            if (certificateField.id == currentValue) {
                ColorStateList.valueOf(ContextCompat.getColor(chip.context, R.color.white))
            } else {
                ColorStateList.valueOf(ContextCompat.getColor(chip.context, R.color.blackTextDark))
            }
        )

        chip.setOnClickListener {
            if (certificateField.id != currentValue) {
                onChange(itemCode, certificateField)
            }
        }
        return chip
    }
}