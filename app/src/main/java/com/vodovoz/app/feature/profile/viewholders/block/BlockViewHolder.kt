package com.vodovoz.app.feature.profile.viewholders.block

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderSliderBlockBinding
import com.vodovoz.app.feature.profile.cats.BLOCKRESPONSE

class BlockViewHolder(
    view: View
) : ItemViewHolder<BLOCKRESPONSE>(view) {

    private val binding: ViewHolderSliderBlockBinding = ViewHolderSliderBlockBinding.bind(view)

    override fun bind(item: BLOCKRESPONSE) {
        super.bind(item)

        binding.tvTitle.text = item.ZAGALOVOK
        binding.tvDescription.text = item.OPISANIE
    }

}