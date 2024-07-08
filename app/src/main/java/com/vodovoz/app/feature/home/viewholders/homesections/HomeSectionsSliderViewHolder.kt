package com.vodovoz.app.feature.home.viewholders.homesections

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentSliderSectionsBinding
import com.vodovoz.app.feature.home.adapter.HomeMainClickListener
import com.vodovoz.app.feature.home.viewholders.homesections.inner.HomeSectionsInnerAdapter
import com.vodovoz.app.feature.home.viewholders.homesections.tabs.HomeSectionsTabsAdapter
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration

class HomeSectionsSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener,
) : ItemViewHolder<HomeSections>(view) {

    private val binding: FragmentSliderSectionsBinding = FragmentSliderSectionsBinding.bind(view)
    private val spaceSections = itemView.resources.getDimension(R.dimen.space_4).toInt()

    private var sectionsSliderAdapter: HomeSectionsInnerAdapter = HomeSectionsInnerAdapter(
        clickListener = { clickListener.onSectionClick(it) },
    )

    private var tabsAdapter: HomeSectionsTabsAdapter = HomeSectionsTabsAdapter(
        clickListener = { clickListener.onSectionsTabClick(it) },
    )

    init {

        val spaceTabs = itemView.resources.getDimension(R.dimen.space_16).toInt()

        binding.rvTabs.addMarginDecoration { rect, viewTab, parent, state ->
            if (parent.getChildAdapterPosition(viewTab) == 0) rect.left = spaceTabs
            if (parent.getChildAdapterPosition(viewTab) == state.itemCount - 1) rect.right = spaceTabs
            else rect.right = spaceTabs / 2
            rect.top = spaceTabs / 2
        }

        binding.rvTabs.adapter = tabsAdapter

        binding.rvTabs.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                onScrollInnerRecycler(this@HomeSectionsSliderViewHolder)
            }
        })

        binding.rvSections.addMarginDecoration { rect, viewDecor, parent, _ ->
            if (parent.getChildAdapterPosition(viewDecor) == 0) {
                rect.left = spaceSections * 4
            } else {
                rect.left = spaceSections
            }
            if (parent.getChildAdapterPosition(viewDecor) == sectionsSliderAdapter.itemCount - 1) {
                rect.right = spaceSections * 4
            } else {
                rect.right = spaceSections
            }
        }

        binding.rvSections.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                onScrollInnerRecycler(this@HomeSectionsSliderViewHolder)
            }
        })


        binding.rvSections.adapter = sectionsSliderAdapter
    }

    override fun getState(): Parcelable? {
        return binding.rvSections.layoutManager?.onSaveInstanceState()
    }

    override fun setState(state: Parcelable) {
        binding.rvSections.layoutManager?.onRestoreInstanceState(state)
    }

    override fun bind(item: HomeSections) {
        super.bind(item)
        if (item.items.parentSectionDataUIList.size == 1) {
            binding.tvName.text = item.items.parentSectionDataUIList.first().title
            binding.rvTabs.visibility = View.GONE
        } else {
            tabsAdapter.submitList(item.items.parentSectionDataUIList)
            binding.rvTabs.visibility = View.VISIBLE
            binding.tvName.visibility = View.GONE
        }

        binding.root.backgroundTintList = ColorStateList.valueOf(Color.parseColor(item.items.color))

        sectionsSliderAdapter.submitList(item.items.parentSectionDataUIList.first { it.isSelected }.sectionDataEntityUIList)
    }

}