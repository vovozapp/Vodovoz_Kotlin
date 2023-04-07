package com.vodovoz.app.feature.bottom.aboutapp.adapter

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class AboutApp(
    val id: Int,
    val actionImgResId: Int,
    val actionNameResId: Int
): Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_about_app_action
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is AboutApp) return false

        return id == item.id
    }
}
