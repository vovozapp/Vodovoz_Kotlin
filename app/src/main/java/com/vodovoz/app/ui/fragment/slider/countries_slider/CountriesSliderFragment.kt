package com.vodovoz.app.ui.fragment.slider.countries_slider

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderCountryBinding
import com.vodovoz.app.ui.adapter.CountriesSliderAdapter
import com.vodovoz.app.ui.base.*
import com.vodovoz.app.ui.diffUtils.CountrySliderDiffUtilCallback
import com.vodovoz.app.ui.interfaces.IOnCountryClick
import com.vodovoz.app.ui.extensions.ViewExtensions.onRenderFinished
import com.vodovoz.app.ui.model.CountryUI
import com.vodovoz.app.ui.model.custom.CountriesSliderBundleUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject


class CountriesSliderFragment : BaseHiddenFragment() {

    private lateinit var countryUIList: List<CountryUI>
    private lateinit var iOnCountryClick: IOnCountryClick
    private lateinit var countriesSliderBundleUI: CountriesSliderBundleUI

    private lateinit var binding: FragmentSliderCountryBinding

    private val compositeDisposable = CompositeDisposable()
    private val onAdapterReadySubject: BehaviorSubject<CountriesSliderBundleUI> = BehaviorSubject.create()
    private val onCountryClickSubject: PublishSubject<Long> = PublishSubject.create()
    private lateinit var countriesSliderAdapter: CountriesSliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeSubjects()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentSliderCountryBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initCountriesRecyclerView()
    }

    private fun initCountriesRecyclerView() {
        binding.countriesRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val space = resources.getDimension(R.dimen.primary_space).toInt()
        binding.countriesRecycler.addItemDecoration(HorizontalMarginItemDecoration(space))
        binding.countriesRecycler.onRenderFinished { width, _ ->
            countriesSliderAdapter = CountriesSliderAdapter(
                onCountryClickSubject = onCountryClickSubject,
                cardWidth = (width - (space * 4))/3
            )
            binding.countriesRecycler.adapter = countriesSliderAdapter
            onAdapterReadySubject.subscribeBy { countriesSliderBundleUI ->
                this.countriesSliderBundleUI = countriesSliderBundleUI
                updateView(countriesSliderBundleUI)
            }.addTo(compositeDisposable)
        }
    }

    private fun subscribeSubjects() {
        onCountryClickSubject.subscribeBy { countryId ->
            iOnCountryClick.onCountryClick(countryId)
            Log.i(LogSettings.ID_LOG, "CID $countryId")
        }.addTo(compositeDisposable)
    }

    fun initCallbacks(iOnCountryClick: IOnCountryClick) {
        this.iOnCountryClick = iOnCountryClick
    }

    fun updateData(countrySliderBundleUI: CountriesSliderBundleUI) {
        onAdapterReadySubject.onNext(countrySliderBundleUI)
    }

    private fun updateView(countrySliderBundleUI: CountriesSliderBundleUI) {
        updateCountriesRecycler(countrySliderBundleUI.countryUIList)

        Glide.with(requireContext())
            .load(countrySliderBundleUI.backgroundPicture)
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

    private fun updateCountriesRecycler(countryUIList: List<CountryUI>) {
        this.countryUIList = countryUIList

        val diffUtil = CountrySliderDiffUtilCallback(
            oldList = countriesSliderAdapter.countryUIList,
            newList = countryUIList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            countriesSliderAdapter.countryUIList = countryUIList
            diffResult.dispatchUpdatesTo(countriesSliderAdapter)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}