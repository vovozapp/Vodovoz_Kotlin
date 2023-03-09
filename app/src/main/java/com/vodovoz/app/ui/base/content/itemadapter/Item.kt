package com.vodovoz.app.ui.base.content.itemadapter

interface Item {

    fun getItemViewType(): Int
    fun areContentsTheSame(item: Item): Boolean
    fun areItemsTheSame(item: Item): Boolean = this == item
    fun getPayload(item: Item): Any? = null
}