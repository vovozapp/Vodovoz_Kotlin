package com.vodovoz.app.common.content.itemadapter

interface Item {

    fun getItemViewType(): Int
    fun areItemsTheSame(item: Item): Boolean
    fun areContentsTheSame(item: Item): Boolean = this == item
    fun getPayload(item: Item): Any? = null
}