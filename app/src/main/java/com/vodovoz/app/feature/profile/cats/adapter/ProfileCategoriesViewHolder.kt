package com.vodovoz.app.feature.profile.cats.adapter

import android.os.Parcelable
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ItemProfileCategoryUiBinding
import com.vodovoz.app.feature.profile.adapter.ProfileFlowClickListener
import com.vodovoz.app.feature.profile.cats.ProfileCategoryUI
import com.vodovoz.app.feature.profile.cats.adapter.inner.ProfileCategoriesInnerAdapter

class ProfileCategoriesViewHolder(
    view: View,
    clickListener: ProfileFlowClickListener,
) : ItemViewHolder<ProfileCategoryUI>(view) {

    private val binding: ItemProfileCategoryUiBinding = ItemProfileCategoryUiBinding.bind(view)
    private val innerAdapter: ProfileCategoriesInnerAdapter =
        ProfileCategoriesInnerAdapter(clickListener)

    init {
        with(binding.insideCategoriesRv) {
            adapter = innerAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun getState(): Parcelable? {
        return binding.insideCategoriesRv.layoutManager?.onSaveInstanceState()
    }

    override fun setState(state: Parcelable) {
        binding.insideCategoriesRv.layoutManager?.onRestoreInstanceState(state)
    }

    override fun bind(item: ProfileCategoryUI) {
        super.bind(item)
        if (item.insideCategories != null) {
            val list = item.insideCategories
            innerAdapter.submitList(list)
            binding.root.isVisible = true
        } else {
            binding.root.isVisible = false
        }
    }
}