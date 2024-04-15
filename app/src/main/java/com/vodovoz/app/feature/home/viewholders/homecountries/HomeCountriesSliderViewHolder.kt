package com.vodovoz.app.feature.home.viewholders.homecountries

import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.FragmentSliderCountryBinding
import com.vodovoz.app.feature.home.adapter.HomeMainClickListener
import com.vodovoz.app.feature.home.viewholders.homecountries.inneradapter.HomeCountriesInnerAdapter
import com.vodovoz.app.feature.home.viewholders.homecountries.inneradapter.HomeCountriesSliderClickListener
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.util.extensions.debugLog

class HomeCountriesSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener,
    width: Int,
) : ItemViewHolder<HomeCountries>(view) {

    private val binding: FragmentSliderCountryBinding = FragmentSliderCountryBinding.bind(view)
    private val space = itemView.resources.getDimension(R.dimen.space_16).toInt()
    private var countriesSliderAdapter: HomeCountriesInnerAdapter = HomeCountriesInnerAdapter(
        clickListener = getHomeCountriesSliderClickListener(),
        cardWidth = (width - (space * 4)) / 2.6
    )

    init {
        binding.rvCountries.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

        binding.rvCountries.addMarginDecoration { rect, viewDecor, parent, _ ->
            if (parent.getChildAdapterPosition(viewDecor) == 0) {
                rect.left = space
            }
            rect.top = space / 2
            rect.right = space / 2
            rect.bottom = (space * 1.2).toInt()
        }

        binding.rvCountries.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                onScrollInnerRecycler(this@HomeCountriesSliderViewHolder)
            }
        })


        binding.rvCountries.adapter = countriesSliderAdapter
    }

    override fun getState(): Parcelable? {
        return binding.rvCountries.layoutManager?.onSaveInstanceState()
    }

    override fun setState(state: Parcelable) {
        debugLog { "setState" }
        binding.rvCountries.layoutManager?.onRestoreInstanceState(state)
    }

    override fun bind(item: HomeCountries) {
        super.bind(item)
        binding.tvName.text = item.items.title

        Glide.with(itemView.context)
            .load(item.items.backgroundPicture)
            .into(object : CustomTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?,
                ) {
                    binding.root.background = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })

        countriesSliderAdapter.submitList(item.items.countryUIList)
    }

    private fun getHomeCountriesSliderClickListener(): HomeCountriesSliderClickListener {
        return object : HomeCountriesSliderClickListener {
            override fun onCountryClick(id: Long) {
                clickListener.onCountryClick(id)
            }
        }
    }

}