package com.vodovoz.app.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderPayMethodBinding
import com.vodovoz.app.ui.model.PayMethodUI

class PayMethodsAdapter : RecyclerView.Adapter<PayMethodVH>() {

    private lateinit var selectPayMethod: (PayMethodUI) -> Unit
    private var payMethodUIList: List<PayMethodUI> = listOf()
    private var selectedPayMethodId: Long = 0

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(
        payMethodUIList: List<PayMethodUI>,
        selectedPayMethodId: Long
    ) {
        this.selectedPayMethodId = selectedPayMethodId
        this.payMethodUIList = payMethodUIList
        notifyDataSetChanged()
    }

    fun setupListeners(selectPayMethod: (PayMethodUI) -> Unit) {
        this.selectPayMethod = selectPayMethod
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PayMethodVH(
        binding = ViewHolderPayMethodBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        selectPayMethod = selectPayMethod
    )

    override fun onBindViewHolder(
        holder: PayMethodVH,
        position: Int
    ) = holder.onBind(payMethodUIList[position], payMethodUIList[position].id == selectedPayMethodId)

    override fun getItemCount() = payMethodUIList.size

}

class PayMethodVH(
    private val binding: ViewHolderPayMethodBinding,
    private val selectPayMethod: (PayMethodUI) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init { binding.root.setOnClickListener { selectPayMethod(payMethodUI) } }

    private lateinit var payMethodUI: PayMethodUI

    fun onBind(payMethodUI: PayMethodUI, isSelected: Boolean) {
        this.payMethodUI = payMethodUI
        binding.tvName.text = payMethodUI.name
        when(isSelected) {
            true -> TextViewCompat.setTextAppearance(binding.tvName, R.style.TextViewNormalBlue)
            false -> TextViewCompat.setTextAppearance(binding.tvName, R.style.TextViewNormalGray)
        }
    }

}