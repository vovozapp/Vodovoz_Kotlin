package com.vodovoz.app.feature.buy_certificate.adapter

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderChooseCertificateBinding
import com.vodovoz.app.databinding.ViewSimpleTextChipBigBinding
import com.vodovoz.app.ui.model.custom.BuyCertificateFieldUI
import com.vodovoz.app.ui.model.custom.BuyCertificatePropertyUI

class ChooseCertificateViewHolder(
    private val binding: ViewHolderChooseCertificateBinding,
    private val onChangeAmount: (String) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

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

    fun onBind(
        property: BuyCertificatePropertyUI,
        chooseOnCertificate: (BuyCertificateFieldUI) -> Unit,
    ) {
        var addStar = ""
        if (property.required) {
            addStar = "*"
        }
        binding.name.text = buildString {
            append(property.name)
            append(addStar)
        }
        val color = if (property.error) {
            R.color.red
        } else {
            R.color.blackTextDark
        }
        binding.name.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, color))

        binding.chipGroupCertificate.removeAllViews()
        property.buyCertificateFieldUIList?.forEach {
            binding.chipGroupCertificate.addView(
                buildQueryChip(it, chooseOnCertificate)
            )
        }

        if (property.showAmount) {
            binding.amountControllerDeployed.visibility = View.VISIBLE
        }

        if (property.text.isNotEmpty()) {
            binding.txtViewText.text = property.text
        } else {
            binding.txtViewText.visibility = View.GONE
        }
    }

    private fun buildQueryChip(
        certificateField: BuyCertificateFieldUI,
        chooseOnCertificate: (BuyCertificateFieldUI) -> Unit,
    ): Chip {
        val chip = ViewSimpleTextChipBigBinding.bind(binding.root).root
        chip.text = certificateField.name
        chip.chipBackgroundColor = if (certificateField.selected) {
            ColorStateList.valueOf(ContextCompat.getColor(chip.context, R.color.bluePrimary))
        } else {
            ColorStateList.valueOf(ContextCompat.getColor(chip.context, R.color.gray))
        }
        chip.setTextColor(
            if (certificateField.selected) {
                ColorStateList.valueOf(ContextCompat.getColor(chip.context, R.color.white))
            } else {
                ColorStateList.valueOf(ContextCompat.getColor(chip.context, R.color.blackTextDark))
            }
        )

        chip.setOnClickListener {
            chooseOnCertificate(certificateField.copy(selected = true))
        }
        return chip
    }
}