package com.vodovoz.app.ui.fragment.home.viewholders.homecountries

import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.vodovoz.app.R
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.databinding.FragmentSliderCountryBinding
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.extensions.ViewExtensions.onRenderFinished
import com.vodovoz.app.ui.fragment.home.adapter.HomeMainClickListener
import com.vodovoz.app.ui.fragment.home.viewholders.homebanners.inneradapter.HomeBannersSliderClickListener
import com.vodovoz.app.ui.fragment.home.viewholders.homecountries.inneradapter.HomeCountriesInnerAdapter
import com.vodovoz.app.ui.fragment.home.viewholders.homecountries.inneradapter.HomeCountriesSliderClickListener
import com.vodovoz.app.ui.model.custom.CountriesSliderBundleUI

class HomeCountriesSliderViewHolder(
    view: View,
    private val clickListener: HomeMainClickListener
) : RecyclerView.ViewHolder(view) {

    private val binding: FragmentSliderCountryBinding = FragmentSliderCountryBinding.bind(view)

    init {

    }

    fun bind(items: HomeCountries) {
        initCountriesRecyclerView(items)
    }

    private fun initCountriesRecyclerView(items: HomeCountries) {
        binding.rvCountries.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

        val space = itemView.resources.getDimension(R.dimen.space_16).toInt()
        binding.rvCountries.addMarginDecoration { rect, view, parent, state ->
            if (parent.getChildAdapterPosition(view) == 0) {
                rect.left = space
            }
            rect.top = space / 2
            rect.right = space
            rect.bottom = space
        }
        binding.rvCountries.onRenderFinished { width, _ ->
            val countriesSliderAdapter = HomeCountriesInnerAdapter(
                clickListener = getHomeCountriesSliderClickListener(),
                cardWidth = (width - (space * 4)) / 3
            ).apply {
                submitList(items.items.countryUIList)
            }
            binding.rvCountries.adapter = countriesSliderAdapter

            binding.tvName.text = items.items.title
            Glide.with(itemView.context)
                .load(items.items.backgroundPicture)
                .into(object : CustomTarget<Drawable?>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable?>?,
                    ) {
                        binding.root.background = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }

    private fun getHomeCountriesSliderClickListener(): HomeCountriesSliderClickListener {
        return object : HomeCountriesSliderClickListener {
            override fun onCountryClick(id: Long) {
                clickListener.onCountryClick(id)
            }
        }
    }

}