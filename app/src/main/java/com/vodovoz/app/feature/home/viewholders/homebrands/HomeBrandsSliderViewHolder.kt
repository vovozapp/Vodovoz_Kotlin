package com.vodovoz.app.feature.home.viewholders.homebrands

import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentSliderBrandBinding
import com.vodovoz.app.feature.home.adapter.HomeMainClickListener
import com.vodovoz.app.feature.home.viewholders.homebrands.inneradapter.HomeBrandsInnerAdapter
import com.vodovoz.app.feature.home.viewholders.homebrands.inneradapter.HomeBrandsSliderClickListener
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration

class HomeBrandsSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener
) : ItemViewHolder<HomeBrands>(view) {

    private val binding: FragmentSliderBrandBinding = FragmentSliderBrandBinding.bind(view)
    private val brandsSliderAdapter = HomeBrandsInnerAdapter(getHomeBrandsSliderClickListener())

    init {
        binding.rvBrands.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

        val space = itemView.resources.getDimension(R.dimen.space_16).toInt()

        binding.rvBrands.adapter = brandsSliderAdapter

        binding.rvBrands.addMarginDecoration { rect, viewDecor, parent, state ->
            if (parent.getChildAdapterPosition(viewDecor) == 0) rect.left = space
            if (parent.getChildAdapterPosition(viewDecor) == state.itemCount - 1) rect.right = space
            else rect.right = space/2
            rect.top = space / 2
            rect.bottom = space / 2
        }

        binding.rvBrands.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                onScrollInnerRecycler(this@HomeBrandsSliderViewHolder)
            }
        })
    }

    override fun getState(): Parcelable? {
        return binding.rvBrands.layoutManager?.onSaveInstanceState()
    }

    override fun setState(state: Parcelable) {
        binding.rvBrands.layoutManager?.onRestoreInstanceState(state)
    }

    override fun bind(item: HomeBrands) {
        super.bind(item)
        brandsSliderAdapter.submitList(item.items)
    }

    private fun getHomeBrandsSliderClickListener() : HomeBrandsSliderClickListener {
        return object : HomeBrandsSliderClickListener {
            override fun onBrandClick(id: Long) {
                clickListener.onBrandClick(id)
            }
        }
    }

}