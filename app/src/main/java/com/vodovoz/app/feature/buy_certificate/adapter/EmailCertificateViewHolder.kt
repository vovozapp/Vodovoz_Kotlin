package com.vodovoz.app.feature.buy_certificate.adapter

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderEmailCertificateBinding
import com.vodovoz.app.ui.model.custom.BuyCertificatePropertyUI
import com.vodovoz.app.util.extensions.debugLog

class EmailCertificateViewHolder(
    private val binding: ViewHolderEmailCertificateBinding,
    private val onEditText: (String, String) -> Unit,
) : ItemViewHolder<BuyCertificatePropertyUI>(binding.root) {


    init{
        binding.email.doAfterTextChanged {
            val itemId = item?.code ?: return@doAfterTextChanged
            debugLog { "doAfterTextChanged $it" }
            onEditText(itemId , it.toString())
        }
    }

    override fun bind(
        item: BuyCertificatePropertyUI
    ) {
        super.bind(item)
        debugLog { "EmailCertificateViewHolder onBind" }
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

        if(binding.email.text.toString() != item.currentValue) {
            binding.email.setText(item.currentValue)
            binding.email.setSelection(item.currentValue.length)
        }

        binding.email.hint = item.value

        if (item.text.isNotEmpty()) {
            binding.txtViewText.text = item.text
        } else {
            binding.txtViewText.visibility = View.GONE
        }
    }
}