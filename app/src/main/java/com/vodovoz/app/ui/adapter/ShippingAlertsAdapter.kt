package com.vodovoz.app.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderShippingAlertBinding
import com.vodovoz.app.ui.model.ShippingAlertUI

class ShippingAlertsAdapter : RecyclerView.Adapter<ShippingAlertVH>() {

    private var shippingAlertUIList: List<ShippingAlertUI> = listOf()
    private lateinit var selectShippingAlert: (ShippingAlertUI) -> Unit

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(shippingAlertUIList: List<ShippingAlertUI>) {
        this.shippingAlertUIList = shippingAlertUIList
        notifyDataSetChanged()
    }

    fun setupListeners(selectShippingAlert: (ShippingAlertUI) -> Unit) {
        this.selectShippingAlert = selectShippingAlert
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ShippingAlertVH(
        binding = ViewHolderShippingAlertBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        selectShippingAlert = selectShippingAlert
    )

    override fun onBindViewHolder(
        holder: ShippingAlertVH,
        position: Int
    ) = holder.onBind(shippingAlertUIList[position])

    override fun getItemCount() = shippingAlertUIList.size

}

class ShippingAlertVH(
    private val binding: ViewHolderShippingAlertBinding,
    private val selectShippingAlert: (ShippingAlertUI) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init { binding.root.setOnClickListener { selectShippingAlert(shippingAlertUI) } }

    private lateinit var shippingAlertUI: ShippingAlertUI

    fun onBind(shippingAlertUI: ShippingAlertUI) {
        this.shippingAlertUI = shippingAlertUI
        binding.tvName.text = shippingAlertUI.name
    }

}