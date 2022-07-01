package com.vodovoz.app.ui.components.fragment.slider.country_slider

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderCountryBinding
import com.vodovoz.app.ui.components.adapter.CountriesSliderAdapter
import com.vodovoz.app.ui.components.base.*
import com.vodovoz.app.ui.components.diffUtils.CountrySliderDiffUtilCallback
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject


class CountrySliderFragment : BaseHiddenFragment() {

    companion object {
        fun newInstance(
            onCountryClickSubject: PublishSubject<Long>,
            viewStateSubject: PublishSubject<ViewState>? = null,
            onUpdateSubject: PublishSubject<Boolean>? = null
        ) = CountrySliderFragment().apply {
            this.viewStateSubject = viewStateSubject
            this.onUpdateSubject = onUpdateSubject
            this.onCountryClickSubject = onCountryClickSubject
        }
    }

    private val compositeDisposable = CompositeDisposable()

    private lateinit var onCountryClickSubject: PublishSubject<Long>
    private var viewStateSubject: PublishSubject<ViewState>? = null
    private var onUpdateSubject: PublishSubject<Boolean>? = null

    private lateinit var binding: FragmentSliderCountryBinding
    private lateinit var viewModel: CountrySliderViewModel

    private lateinit var countriesSliderAdapter: CountriesSliderAdapter

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
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[CountrySliderViewModel::class.java]
        viewModel.updateData()
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
                        countriesSliderAdapter = CountriesSliderAdapter(
                            onCountryClickSubject = onCountryClickSubject,
                            cardWidth = (binding.countriesRecycler.width - (space * 4))/3
                        )
                        binding.countriesRecycler.adapter = countriesSliderAdapter
                        observeViewModel()
                    }
                }
            }
        )
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            Log.i(LogSettings.ID_LOG, "COUNTRY ${state::class.simpleName}")
            when(state) {
                is ViewState.Hide -> {
                    viewStateSubject?.onNext(state)
                    hide()
                }
                else -> viewStateSubject?.onNext(state)
            }
        }

        viewModel.countryUIListLD.observe(viewLifecycleOwner) { countryUIDataList ->
            val diffUtil = CountrySliderDiffUtilCallback(
                oldList = countriesSliderAdapter.countryUIList,
                newList = countryUIDataList
            )

            DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                countriesSliderAdapter.countryUIList = countryUIDataList
                diffResult.dispatchUpdatesTo(countriesSliderAdapter)
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
    }

    override fun onStart() {
        super.onStart()
        onUpdateSubject?.subscribeBy {
            viewModel.updateData()
        }?.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

}