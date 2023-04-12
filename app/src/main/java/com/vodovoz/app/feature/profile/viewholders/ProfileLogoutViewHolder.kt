package com.vodovoz.app.feature.profile.viewholders

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ItemProfileLogoutBinding
import com.vodovoz.app.feature.profile.adapter.ProfileFlowClickListener
import com.vodovoz.app.feature.profile.viewholders.models.ProfileLogout

class ProfileLogoutViewHolder(
    view: View,
    clickListener: ProfileFlowClickListener
) : ItemViewHolder<ProfileLogout>(view) {

    private val binding: ItemProfileLogoutBinding = ItemProfileLogoutBinding.bind(view)

    init {
        binding.root.setOnClickListener {
            clickListener.logout()
        }
    }

}