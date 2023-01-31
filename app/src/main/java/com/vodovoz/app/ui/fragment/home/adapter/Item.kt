package com.vodovoz.app.ui.fragment.home.adapter

interface Item {
    fun getItemViewType(): Int
    fun areItemsTheSame(item: Item): Boolean
    fun areContentsTheSame(item: Item): Boolean
}