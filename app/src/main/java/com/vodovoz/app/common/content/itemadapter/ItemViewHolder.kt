package com.vodovoz.app.common.content.itemadapter

import android.os.Parcelable
import android.view.View
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class ItemViewHolder<I : Item>(view: View) : RecyclerView.ViewHolder(view), CoroutineScope {

    private var job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    @Suppress("UNCHECKED_CAST")
    val item: I?
        get() {
            return (bindingAdapter as? ItemAdapter)?.getItem(bindingAdapterPosition) as? I
        }


    @CallSuper
    open fun bind(item: I) {

    }

    open fun bind(item: I, payloads: List<Any>) {

    }

    @CallSuper
    open fun attach() {

    }

    @CallSuper
    open fun detach() {
        job.cancel()
        job = Job()
    }

    open fun getState(): Parcelable? = null

    open fun setState(state: Parcelable) {}

    var onScrollInnerRecycler: (ItemViewHolder<out I>) -> Unit = {}
}