package com.vodovoz.app.feature.buy_certificate.adapter

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DividerItemDecoration
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderChooseCertificateBinding
import com.vodovoz.app.feature.buy_certificate.adapter.inner.CertificateAdapter
import com.vodovoz.app.ui.model.custom.BuyCertificateFieldUI
import com.vodovoz.app.ui.model.custom.BuyCertificatePropertyUI

class ChooseCertificateViewHolder(
    private val binding: ViewHolderChooseCertificateBinding,
    private val onChangeAmount: (String) -> Unit,
    private val onChange: (String, BuyCertificateFieldUI) -> Unit,
) : ItemViewHolder<BuyCertificatePropertyUI>(binding.root) {

    private val certAdapter: CertificateAdapter by lazy {
        CertificateAdapter(
            buyCertificatePropertyUI?.buyCertificateFieldUIList ?: emptyList()
        ) { field ->
            item?.code?.let { code ->
                onChange(code, field)
                buyCertificatePropertyUI = buyCertificatePropertyUI?.copy(
                    buyCertificateFieldUIList = buyCertificatePropertyUI?.buyCertificateFieldUIList?.map {
                        it.copy(
                            isSelected = it.id == field.id
                        )
                    },
                    error = false
                )
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
                certAdapter.list =
                    buyCertificatePropertyUI?.buyCertificateFieldUIList ?: emptyList()
                certAdapter.notifyDataSetChanged()
            }
        }
    }

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

    private var buyCertificatePropertyUI: BuyCertificatePropertyUI? = null


    override fun bind(item: BuyCertificatePropertyUI) {
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

            binding.certificateRecyclerView.adapter = certAdapter
            val divider =
                DividerItemDecoration(binding.root.context, DividerItemDecoration.HORIZONTAL)
            ContextCompat.getDrawable(binding.root.context, R.drawable.horizontal_divider_8)
                ?.let { divider.setDrawable(it) }
            if (binding.certificateRecyclerView.itemDecorationCount != 0) {
                binding.certificateRecyclerView.removeItemDecorationAt(0)
            }
            binding.certificateRecyclerView.addItemDecoration(divider)
            certAdapter.list = property.buyCertificateFieldUIList ?: emptyList()
            certAdapter.notifyDataSetChanged()

            if (property.showAmount) {
                binding.amountControllerDeployed.visibility = View.VISIBLE
            }

            if (property.text.isNotEmpty()) {
                binding.txtViewText.text = property.text
            } else {
                binding.txtViewText.visibility = View.GONE
            }
        }
    }
}