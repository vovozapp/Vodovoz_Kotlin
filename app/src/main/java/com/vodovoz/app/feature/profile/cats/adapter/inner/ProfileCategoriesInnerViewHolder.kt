package com.vodovoz.app.feature.profile.cats.adapter.inner

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
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
                "url1" -> { clickListener.onUrlClick(item.url) }
                "url2" -> { clickListener.onUrlTwoClick(item.url)}
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
                "newprilvoda" -> { clickListener.onNewWaterApp() }
                "polychskidka" -> { clickListener.onFetchDiscount() }
                "activsertivicat" -> { clickListener.onActiveCertificate() }
                "pokypkasertificat" -> { clickListener.onBuyCertificate() }
                "rozikrish" -> { clickListener.onSpoofClick(item.name, item.url) }
            }
        }
    }

    override fun bind(item: ProfileInsideCategoryUI) {
        super.bind(item)

        binding.insideCategoryTv.text = item.name
    }

}