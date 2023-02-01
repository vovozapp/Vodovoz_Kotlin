package com.vodovoz.app.ui.fragment.home.viewholders.homebottominfo

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.FragmentSectionAdditionalInfoBinding
import com.vodovoz.app.ui.fragment.home.adapter.HomeMainClickListener

class HomeBottomInfoViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener
) : RecyclerView.ViewHolder(view) {

    private val binding: FragmentSectionAdditionalInfoBinding = FragmentSectionAdditionalInfoBinding.bind(view)

    init {
        binding.aboutApp.setOnClickListener { clickListener.onAboutAppClick() }
        binding.aboutDelivery.setOnClickListener { clickListener.onAboutDeliveryClick() }
        binding.aboutPay.setOnClickListener { clickListener.onAboutPayClick() }
        binding.aboutShop.setOnClickListener { clickListener.onAboutShopClick() }
        binding.services.setOnClickListener { clickListener.onServicesClick() }
        binding.contacts.setOnClickListener { clickListener.onContactsClick() }
        binding.howToOrder.setOnClickListener { clickListener.onHowToOrderClick() }
    }

    fun bind() {

    }
}