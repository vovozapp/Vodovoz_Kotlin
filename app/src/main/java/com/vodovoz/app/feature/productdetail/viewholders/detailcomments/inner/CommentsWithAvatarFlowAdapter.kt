package com.vodovoz.app.feature.productdetail.viewholders.detailcomments.inner

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder

class CommentsWithAvatarFlowAdapter : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when(viewType) {
            R.layout.view_holder_slider_comment -> {
                CommentsWithAvatarFlowViewHolder(getViewFromInflater(viewType, parent))
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}