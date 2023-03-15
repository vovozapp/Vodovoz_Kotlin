package com.vodovoz.app.common.content.itemadapter

import android.view.View
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView

abstract class ItemViewHolder<I : Item>(view: View) : RecyclerView.ViewHolder(view) {

    @CallSuper
    open fun bind(item: I) {

    }

    open fun bind(item: I, payloads: List<Any>) {

    }

}