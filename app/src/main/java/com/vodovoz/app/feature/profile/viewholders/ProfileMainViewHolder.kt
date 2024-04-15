package com.vodovoz.app.feature.profile.viewholders

import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ItemProfileMainRvBinding
import com.vodovoz.app.feature.profile.adapter.ProfileFlowClickListener
import com.vodovoz.app.feature.profile.cats.adapter.ProfileCategoriesAdapter
import com.vodovoz.app.feature.profile.viewholders.models.ProfileMain

class ProfileMainViewHolder(
    view: View,
    clickListener: ProfileFlowClickListener
) : ItemViewHolder<ProfileMain>(view) {

    private val binding: ItemProfileMainRvBinding = ItemProfileMainRvBinding.bind(view)
    private val categoriesAdapter = ProfileCategoriesAdapter(clickListener)

    init {
        with(binding.profileMainRv) {
            adapter = categoriesAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun getState(): Parcelable? {
        return binding.profileMainRv.layoutManager?.onSaveInstanceState()
    }

    override fun setState(state: Parcelable) {
        binding.profileMainRv.layoutManager?.onRestoreInstanceState(state)
    }

    override fun bind(item: ProfileMain) {
        super.bind(item)

        val list = item.items
        if (list != null) {
            categoriesAdapter.submitList(list)
        }
    }
}