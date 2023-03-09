package com.vodovoz.app.ui.base.content.itemadapter

import androidx.recyclerview.widget.DiffUtil

class ItemDiffCallback(
    private val oldItems: List<Item>,
    private val newItems: List<Item>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].areItemsTheSame(newItems[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].areContentsTheSame(newItems[newItemPosition])
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return oldItems[oldItemPosition].getPayload(newItems[newItemPosition])
    }
}