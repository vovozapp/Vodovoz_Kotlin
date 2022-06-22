package com.vodovoz.app.ui.components.fragment.countrySlider

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderCountryBinding
import com.vodovoz.app.ui.components.base.HorizontalMarginItemDecoration
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.adapter.countrySliderAdapter.CountrySliderAdapter
import com.vodovoz.app.ui.components.adapter.countrySliderAdapter.CountrySliderDiffUtilCallback
import com.vodovoz.app.ui.components.fragment.home.HomeFragmentDirections
import com.vodovoz.app.ui.components.fragment.productsWithoutFilter.ProductsWithoutFiltersFragment
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject


class CountrySliderFragment : Fragment() {

    private lateinit var binding: FragmentSliderCountryBinding
    private lateinit var viewModel: CountrySliderViewModel

    private val compositeDisposable = CompositeDisposable()

    private val onCountryClickSubject: PublishSubject<Long> = PublishSubject.create()
    private lateinit var countrySliderAdapter: CountrySliderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentSliderCountryBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initCountriesRecyclerView()
        initViewModel()
    }.root

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[CountrySliderViewModel::class.java]
    }

    private fun initCountriesRecyclerView() {
        binding.countriesRecycler.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        val space = resources.getDimension(R.dimen.primary_space).toInt()
        binding.countriesRecycler.addItemDecoration(HorizontalMarginItemDecoration(space))
        binding.countriesRecycler.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (binding.countriesRecycler.width != 0) {
                        binding.countriesRecycler.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        countrySliderAdapter = CountrySliderAdapter(
                            onCountryClickSubject = onCountryClickSubject,
                            cardWidth = (binding.countriesRecycler.width - (space * 4))/3
                        )
                        binding.countriesRecycler.adapter = countrySliderAdapter
                        observeViewModel()
                    }
                }
            }
        )
    }

    private fun observeViewModel() {
        viewModel.countryUIListLD.observe(viewLifecycleOwner) { countryUIDataList ->
            val diffUtil = CountrySliderDiffUtilCallback(
                oldList = countrySliderAdapter.countryUIList,
                newList = countryUIDataList
            )

            DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                countrySliderAdapter.countryUIList = countryUIDataList
                diffResult.dispatchUpdatesTo(countrySliderAdapter)
            }
        }

        viewModel.titleLD.observe(viewLifecycleOwner) { title ->
            binding.title.text = title
        }

        viewModel.backgroundImageUrlLD.observe(viewLifecycleOwner) { imageUrl ->
            Glide.with(requireContext())
                .load(imageUrl)
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

        viewModel.sateHideLD.observe(viewLifecycleOwner) { stateHide ->
            when(stateHide) {
                true -> binding.root.visibility = View.VISIBLE
                false -> binding.root.visibility = View.GONE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        onCountryClickSubject.subscribeBy { countryId ->
            parentFragment?.findNavController()?.navigate(
                HomeFragmentDirections.actionHomeFragmentToProductsWithoutFiltersFragment(
                    ProductsWithoutFiltersFragment.DataSource.Country(countryId)
                )
            )
        }.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

}