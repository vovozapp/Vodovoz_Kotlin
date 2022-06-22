package com.vodovoz.app.ui.components.fragment.allProductFilters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.databinding.FragmentFilterListBinding
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.components.base.FetchStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.adapter.allProductFiltersAdapter.FilterDiffUtilCallback
import com.vodovoz.app.ui.components.adapter.allProductFiltersAdapter.FiltersAdapter
import com.vodovoz.app.ui.components.fragment.products.ProductsFragment
import com.vodovoz.app.ui.model.FilterPriceUI
import com.vodovoz.app.ui.model.FilterUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject


class FiltersFragment : FetchStateBaseFragment() {

    companion object {
        const val CONCRETE_FILTER = "CONCRETE_FILTER"
    }

    private lateinit var binding: FragmentFilterListBinding
    private lateinit var viewModel: FiltersViewModel

    private val onFilterClickSubject: PublishSubject<FilterUI> = PublishSubject.create()
    private val onFilterClearClickSubject: PublishSubject<FilterUI> = PublishSubject.create()
    private val compositeDisposable = CompositeDisposable()

    private val filtersAdapter = FiltersAdapter(
        onFilterClickSubject = onFilterClickSubject,
        onFilterClearClickSubject = onFilterClearClickSubject
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        getArgs()
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup,
    ) = FragmentFilterListBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
    }.root

    override fun initView() {
        initViewModel()
        initActionBar()
        initFilterRecycler()
        initFilterPrice()
        initAppBar()
        initBottomButtons()
        observeViewModel()
        observeResultLiveData()
    }

    override fun update() {
        viewModel.updateData()
    }

    private fun initAppBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.let { noNullActionBar ->
            noNullActionBar.setDisplayHomeAsUpEnabled(true)
            noNullActionBar.setDisplayShowHomeEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[FiltersViewModel::class.java]
    }

    private fun getArgs() {
        with(FiltersFragmentArgs.fromBundle(requireArguments())) {
            viewModel.setArgs(defaultFilterBundle, categoryId)
        }
    }

    private fun initActionBar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun initFilterRecycler() {
        onFilterClickSubject.subscribeBy { filter ->
            findNavController().navigate(FiltersFragmentDirections.actionFiltersFragmentToConcreteFilterFragment(
                viewModel.categoryId!!,
                filter
            ))
        }.addTo(compositeDisposable)

        onFilterClearClickSubject.subscribeBy { filter ->
            viewModel.removeConcreteFilter(filter)
        }.addTo(compositeDisposable)

        binding.filterRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.filterRecycler.adapter = filtersAdapter
    }

    private fun initFilterPrice() {
        binding.priceSlider.addOnChangeListener { rangeSlider, _, _ ->
            binding.minPrice.text = rangeSlider.values.first().toInt().toString()
            binding.maxPrice.text = rangeSlider.values.last().toInt().toString()

            viewModel.customFilterBundle!!.filterPriceUI.minPrice =
                if (rangeSlider.values.first().toInt() == rangeSlider.valueFrom.toInt()) Int.MIN_VALUE
                else rangeSlider.values.first().toInt()

            viewModel.customFilterBundle!!.filterPriceUI.maxPrice =
                if (rangeSlider.values.last().toInt() == rangeSlider.valueTo.toInt()) Int.MAX_VALUE
                else rangeSlider.values.last().toInt()        }
    }

    private fun initBottomButtons() {
        binding.apply.setOnClickListener { sendFilterBundleBack() }
        binding.clear.setOnClickListener {
            viewModel.clearCustomFilterBundle()
            sendFilterBundleBack()
        }
    }

    private fun sendFilterBundleBack() {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            ProductsFragment.FILTER_BUNDLE,
            viewModel.customFilterBundle
        )
        findNavController().popBackStack()
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<FilterUI>(CONCRETE_FILTER)?.observe(viewLifecycleOwner) { filter ->
                changeConcreteFilter(filter)
            }
    }

    private fun observeViewModel() {
        viewModel.fetchStateLD.observe(viewLifecycleOwner) { fetchState ->
            when (fetchState) {
                is FetchState.Loading -> onStateLoading()
                is FetchState.Error -> onStateError(fetchState.errorMessage)
                is FetchState.Success -> {
                    onStateSuccess()
                    fetchState.data?.let {
                        fillFilterPrice(fetchState.data.filterPriceUI)
                        fillFilterList(fetchState.data.filterUIList)
                    }
                }
            }
        }
    }

    private fun fillFilterList(filterList: List<FilterUI>) {
        val diffUtil = FilterDiffUtilCallback(
            oldList = filtersAdapter.filterList,
            newList = filterList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            filtersAdapter.filterList = filterList.toMutableList()
            diffResult.dispatchUpdatesTo(filtersAdapter)
        }
    }

    private fun changeConcreteFilter(filterUI: FilterUI) {
        viewModel.changeConcreteFilter(filterUI)
    }

    private fun fillFilterPrice(filterPriceUI: FilterPriceUI) {
        with(binding) {
            priceSlider.valueFrom = filterPriceUI.minPrice.toFloat()
            priceSlider.valueTo = filterPriceUI.maxPrice.toFloat()

            val currentMinPrice =
                if (viewModel.customFilterBundle!!.filterPriceUI.minPrice == Int.MIN_VALUE) filterPriceUI.minPrice
                else viewModel.customFilterBundle!!.filterPriceUI.minPrice

            val currentMaxPrice =
                if (viewModel.customFilterBundle!!.filterPriceUI.maxPrice == Int.MAX_VALUE) filterPriceUI.maxPrice
                else viewModel.customFilterBundle!!.filterPriceUI.maxPrice

            priceSlider.values = listOf(
                currentMinPrice.toFloat(),
                currentMaxPrice.toFloat()
            )

            minPrice.text = currentMinPrice.toString()
            maxPrice.text = currentMaxPrice.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}