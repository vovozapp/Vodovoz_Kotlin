package com.vodovoz.app.feature.home.viewholders.homepopulars

import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentSliderPopularBinding
import com.vodovoz.app.feature.home.adapter.HomeMainClickListener
import com.vodovoz.app.feature.home.viewholders.homepopulars.inneradapter.HomePopularCategoriesSliderClickListener
import com.vodovoz.app.feature.home.viewholders.homepopulars.inneradapter.HomePopularsInnerAdapter
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration

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

        binding.rvPopularCategories.addMarginDecoration { rect, viewDecor, parent, state ->
            if (parent.getChildAdapterPosition(viewDecor) == 0) rect.left = space
            if (parent.getChildAdapterPosition(viewDecor) == state.itemCount - 1) rect.right = space
            else rect.right = space/2
            rect.top = space / 2
            rect.bottom = space / 2
        }

            binding.rvPopularCategories.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    onScrollInnerRecycler(this@HomePopularCategoriesSliderViewHolder)
                }
            })

        binding.rvPopularCategories.adapter = homePopularsAdapter
    }

    override fun getState(): Parcelable? {
        return binding.rvPopularCategories.layoutManager?.onSaveInstanceState()
    }

    override fun setState(state: Parcelable) {
        binding.rvPopularCategories.layoutManager?.onRestoreInstanceState(state)
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