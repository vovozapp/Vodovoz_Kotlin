package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderBottleNameBinding
import com.vodovoz.app.ui.diffUtils.BottleDiffUtilCallback
import com.vodovoz.app.ui.model.BottleUI
import com.vodovoz.app.ui.view_holder.BottleNameViewHolder
import io.reactivex.rxjava3.subjects.PublishSubject

class AllBottlesAdapter(
    private val onBottleClickSubject: PublishSubject<Long>
) : RecyclerView.Adapter<BottleNameViewHolder>() {

    var lastQuery: String = ""

    var bottleUIFullList = listOf<BottleUI>()
        set(value) {
            field = value
            filter(lastQuery)
        }

    private var bottleUIFilteredList = mutableListOf<BottleUI>()

    fun filter(query: String) {
        lastQuery = query
        val newFilteredList = bottleUIFullList.filter { it.name.contains(query) }
        updateData(newFilteredList)
    }

    private fun updateData(bottleUIList: List<BottleUI>) {
        val diffUtil = BottleDiffUtilCallback(
            oldList = bottleUIFilteredList,
            newList = bottleUIList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            bottleUIFilteredList = bottleUIList.toMutableList()
            diffResult.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = BottleNameViewHolder(
        binding = ViewHolderBottleNameBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onBottleClickSubject = onBottleClickSubject
    )

    override fun onBindViewHolder(
        holder: BottleNameViewHolder,
        position: Int
    ) = holder.onBind(bottleUIFilteredList[position])

    override fun getItemCount() = bottleUIFilteredList.size

}