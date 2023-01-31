package com.vodovoz.app.ui.fragment.home.adapter

import androidx.recyclerview.widget.DiffUtil

class HomeMainDiffUtilCallback : DiffUtil.ItemCallback<Item>() {

    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.areItemsTheSame(newItem)
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.areContentsTheSame(newItem)
    }
}