package com.vodovoz.app.feature.buy_certificate.adapter

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderEmailCertificateBinding
import com.vodovoz.app.ui.model.custom.BuyCertificatePropertyUI

class EmailCertificateViewHolder(
    private val binding: ViewHolderEmailCertificateBinding,
    private val onEditText: (String, String) -> Unit,
) : ItemViewHolder<BuyCertificatePropertyUI>(binding.root) {


    init {
        binding.email.doAfterTextChanged {
            val itemId = item?.code ?: return@doAfterTextChanged
            onEditText(itemId, it.toString())
            buyCertificatePropertyUI?.let { property ->
                val color = if (property.error) {
                    R.color.red
                } else {
                    R.color.blackTextDark
                }
                binding.name.setTextColor(
                    ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, color))
                )
            }
            buyCertificatePropertyUI = buyCertificatePropertyUI?.copy(
                error = false,
                currentValue = it.toString()
            )
        }
    }

    private var buyCertificatePropertyUI: BuyCertificatePropertyUI? = null

    override fun bind(
        item: BuyCertificatePropertyUI,
    ) {
        super.bind(item)
        buyCertificatePropertyUI = item
        buyCertificatePropertyUI?.let { property ->
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
            binding.name.setTextColor(
                ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, color))
            )

            if (binding.email.text.toString() != property.currentValue) {
                binding.email.setText(property.currentValue)
                binding.email.setSelection(property.currentValue.length)
            }

            binding.email.hint = property.value

            if (property.text.isNotEmpty()) {
                binding.txtViewText.text = property.text
            } else {
                binding.txtViewText.visibility = View.GONE
            }
        }
    }

}