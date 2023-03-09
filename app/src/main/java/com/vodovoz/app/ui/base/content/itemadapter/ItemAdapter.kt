package com.vodovoz.app.ui.base.content.itemadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

@Suppress("UNCHECKED_CAST")
abstract class ItemAdapter : RecyclerView.Adapter<ItemViewHolder<out Item>>() {

    private val items = mutableListOf<Item>()

    private val adapterListUpdateCallback by lazy { AdapterListUpdateCallback(this) }

    override fun getItemViewType(position: Int): Int {
        return items[position].getItemViewType()
    }

    fun submitList(items: List<Item>) {
        val diff = DiffUtil.calculateDiff(ItemDiffCallback(this.items, items), false)
        diff.dispatchUpdatesTo(adapterListUpdateCallback)

        this.items.clear()
        this.items.addAll(items)
    }

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item>

    override fun onBindViewHolder(holder: ItemViewHolder<out Item>, position: Int) {
        holder as ItemViewHolder<in Item>
        holder.bind(items[position])
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder<out Item>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            holder as ItemViewHolder<in Item>
            holder.bind(items[position], payloads)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getItem(position: Int): Item? {
        return items.getOrNull(position)
    }

    fun getViewFromInflater(layoutId: Int, parent: ViewGroup): View {
        return LayoutInflater
            .from(parent.context)
            .inflate(layoutId, parent, false)
    }

}