package com.vodovoz.app.feature.profile.viewholders

import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ItemProfileHeaderBinding
import com.vodovoz.app.feature.profile.adapter.ProfileFlowClickListener
import com.vodovoz.app.feature.profile.viewholders.models.ProfileHeader

class ProfileHeaderViewHolder(
    view: View,
    clickListener: ProfileFlowClickListener
) : ItemViewHolder<ProfileHeader>(view) {

    private val binding: ItemProfileHeaderBinding = ItemProfileHeaderBinding.bind(view)

    init {
        binding.root.setOnClickListener { 
            clickListener.onHeaderClick()
        }
    }

    override fun bind(item: ProfileHeader) {
        super.bind(item)

        Glide.with(itemView.context)
            .load(item.data.avatar)
            .placeholder(ContextCompat.getDrawable(itemView.context, R.drawable.circleimageprofile))
            .into(binding.avatar)

        binding.name.text = StringBuilder()
            .append(item.data.firstName)
            .append(" ")
            .append(item.data.secondName)
            .toString()
    }
}