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
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.util.extensions.debugLog

class HomeSectionsSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener,
) : ItemViewHolder<HomeSections>(view) {

    private val binding: FragmentSliderSectionsBinding = FragmentSliderSectionsBinding.bind(view)
    private val space = itemView.resources.getDimension(R.dimen.space_4).toInt()
    private var sectionsSliderAdapter: HomeSectionsInnerAdapter = HomeSectionsInnerAdapter(
        clickListener = { clickListener.onSectionClick(it) },
    )

    init {

        binding.rvSections.addMarginDecoration { rect, viewDecor, parent, _ ->
            if (parent.getChildAdapterPosition(viewDecor) == 0) {
                rect.left = space * 4
            } else {
                rect.left = space
            }
            if (parent.getChildAdapterPosition(viewDecor) == sectionsSliderAdapter.itemCount - 1) {
                rect.right = space * 4
            } else {
                rect.right = space
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
        debugLog { "setState" }
        binding.rvSections.layoutManager?.onRestoreInstanceState(state)
    }

    override fun bind(item: HomeSections) {
        super.bind(item)
        binding.tvName.text = item.items.parentSectionDataUIList.first().title

        binding.root.backgroundTintList = ColorStateList.valueOf(Color.parseColor(item.items.color))
//        Glide.with(itemView.context)
//            .load(item.items.backgroundPicture)
//            .into(object : CustomTarget<Drawable?>() {
//                override fun onResourceReady(
//                    resource: Drawable,
//                    transition: Transition<in Drawable?>?,
//                ) {
//                    binding.root.background = resource
//                }
//
//                override fun onLoadCleared(placeholder: Drawable?) {}
//            })

        sectionsSliderAdapter.submitList(item.items.parentSectionDataUIList.first().sectionDataEntityUIList)
    }

}