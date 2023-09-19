package com.vodovoz.app.feature.home.viewholders.homebottominfo

import android.view.View
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentSectionAdditionalInfoBinding
import com.vodovoz.app.feature.home.adapter.HomeMainClickListener

class HomeBottomInfoViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener
) : ItemViewHolder<Item>(view) {

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