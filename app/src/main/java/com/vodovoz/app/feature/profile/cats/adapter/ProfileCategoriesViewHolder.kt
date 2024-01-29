package com.vodovoz.app.feature.profile.cats.adapter

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ItemProfileCategoryUiBinding
import com.vodovoz.app.feature.profile.adapter.ProfileFlowClickListener
import com.vodovoz.app.feature.profile.cats.ProfileCategoryUI
import com.vodovoz.app.feature.profile.cats.ProfileInsideCategoryUI
import com.vodovoz.app.feature.profile.cats.adapter.inner.ProfileCategoriesInnerAdapter

class ProfileCategoriesViewHolder(
    view: View,
    clickListener: ProfileFlowClickListener
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

    override fun bind(item: ProfileCategoryUI) {
        super.bind(item)


        if (item.insideCategories != null) {
            val list = item.insideCategories.toMutableList()
            if (item.title.isNullOrEmpty().not()) {
                binding.categoryTitleTv.text = item.title
                binding.categoryTitleTv.isVisible = true
            } else {
                binding.categoryTitleTv.isVisible = false
            }
            if(item.title == "Личный счет") {
                list.add(
                    ProfileInsideCategoryUI(
                        id = "certificate",
                        name = "Активация сертификата",
                        url = null,
                        amount = null,
                        chatUiList = null
                    )
                )
            }
            innerAdapter.submitList(list)
            binding.root.isVisible = true
        } else {
            binding.root.isVisible = false
        }
    }
}