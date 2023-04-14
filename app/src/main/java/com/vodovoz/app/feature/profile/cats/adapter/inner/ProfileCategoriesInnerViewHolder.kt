package com.vodovoz.app.feature.profile.cats.adapter.inner

import android.view.View
import androidx.core.view.isVisible
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ItemProfileInsideCategoryUiBinding
import com.vodovoz.app.feature.profile.adapter.ProfileFlowClickListener
import com.vodovoz.app.feature.profile.cats.ProfileInsideCategoryUI

class ProfileCategoriesInnerViewHolder(
    view: View,
    clickListener: ProfileFlowClickListener
) : ItemViewHolder<ProfileInsideCategoryUI>(view) {

    private val binding: ItemProfileInsideCategoryUiBinding = ItemProfileInsideCategoryUiBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            val item = item ?:return@setOnClickListener
            when(item.id) {
                "moyadress" -> { clickListener.onAddressesClick() }
                "url1" -> { clickListener.onUrlClick() }
                "moyzakaz" -> { clickListener.onOrdersHistoryClick() }
                "kyptovary" -> { clickListener.onLastPurchasesClick() }
                "remont" -> { clickListener.onRepairClick() }
                "sanitar" -> { clickListener.onOzonClick() }
                "moyanketa" -> { clickListener.onQuestionnaireClick() }
                "moyevedomleniya" -> { clickListener.onNotificationsClick() }
                "moydostavka" -> { clickListener.onAboutDeliveryClick() }
                "moyoplata" -> { clickListener.onAboutPaymentClick() }
                "moychat" -> { clickListener.onMyChatClick() }
                "moybezopasnost" -> { clickListener.onSafetyClick() }
                "oprile" -> { clickListener.onAboutAppClick() }
            }
        }
    }

    override fun bind(item: ProfileInsideCategoryUI) {
        super.bind(item)

        binding.insideCategoryTv.text = item.name

        if(item.amount != null) {
            binding.amountTv.text = item.amount.toString()
            binding.amountTv.isVisible = true
        } else {
            binding.amountTv.isVisible = false
        }
    }

}