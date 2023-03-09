package com.vodovoz.app.ui.fragment.home.viewholders.homepopulars

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderPopularBinding
import com.vodovoz.app.ui.base.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.fragment.home.adapter.HomeMainClickListener
import com.vodovoz.app.ui.fragment.home.viewholders.homepopulars.inneradapter.HomePopularCategoriesSliderClickListener
import com.vodovoz.app.ui.fragment.home.viewholders.homepopulars.inneradapter.HomePopularsInnerAdapter

class HomePopularCategoriesSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener
) : ItemViewHolder<HomePopulars>(view) {

    private val binding: FragmentSliderPopularBinding = FragmentSliderPopularBinding.bind(view)
    private val homePopularsAdapter = HomePopularsInnerAdapter(getHomePopularsSliderClickListener())

    init {
        binding.rvPopularCategories.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

        val space = itemView.resources.getDimension(R.dimen.space_16).toInt()

        binding.rvPopularCategories.addMarginDecoration { rect, view, parent, state ->
            if (parent.getChildAdapterPosition(view) == 0) rect.left = space
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) rect.right = space
            else rect.right = space/2
            rect.top = space / 2
            rect.bottom = space / 2
        }

        binding.rvPopularCategories.adapter = homePopularsAdapter
    }

    override fun bind(item: HomePopulars) {
        super.bind(item)
        homePopularsAdapter.submitList(item.items)
    }

    private fun getHomePopularsSliderClickListener() : HomePopularCategoriesSliderClickListener {
        return object : HomePopularCategoriesSliderClickListener {
            override fun onCategoryClick(id: Long?) {
                clickListener.onCategoryClick(id)
            }
        }
    }

}