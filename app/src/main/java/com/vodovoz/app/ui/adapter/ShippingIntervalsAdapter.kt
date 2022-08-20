package com.vodovoz.app.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderPayMethodBinding
import com.vodovoz.app.databinding.ViewHolderShippingIntervalBinding
import com.vodovoz.app.ui.model.ShippingIntervalUI

class ShippingIntervalsAdapter : RecyclerView.Adapter<ShippingIntervalVH>() {

    private var shippingIntervalUIList: List<ShippingIntervalUI> = listOf()
    private lateinit var selectShippingInterval: (ShippingIntervalUI) -> Unit

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(shippingIntervalUIList: List<ShippingIntervalUI>) {
        this.shippingIntervalUIList = shippingIntervalUIList
        notifyDataSetChanged()
    }

    fun setupListeners(selectShippingInterval: (ShippingIntervalUI) -> Unit) {
        this.selectShippingInterval = selectShippingInterval
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ShippingIntervalVH(
        binding = ViewHolderShippingIntervalBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        selectShippingInterval = selectShippingInterval
    )

    override fun onBindViewHolder(
        holder: ShippingIntervalVH,
        position: Int
    ) = holder.onBind(
        shippingIntervalUI = shippingIntervalUIList[position] )

    override fun getItemCount() = shippingIntervalUIList.size

}

class ShippingIntervalVH(
    private val binding: ViewHolderShippingIntervalBinding,
    private val selectShippingInterval: (ShippingIntervalUI) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init { binding.root.setOnClickListener { selectShippingInterval(shippingIntervalUI) } }

    private lateinit var shippingIntervalUI: ShippingIntervalUI

    fun onBind(shippingIntervalUI: ShippingIntervalUI) {
        this.shippingIntervalUI = shippingIntervalUI
        binding.tvName.text = shippingIntervalUI.name
    }
}

