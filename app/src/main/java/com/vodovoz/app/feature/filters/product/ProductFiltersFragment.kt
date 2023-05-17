package com.vodovoz.app.feature.filters.product

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentFilterListBinding
import com.vodovoz.app.ui.adapter.ProductFiltersAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseDialogFragment
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.feature.productlist.PaginatedProductsCatalogFragment
import com.vodovoz.app.ui.model.FilterPriceUI
import com.vodovoz.app.ui.model.FilterUI
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

@AndroidEntryPoint
class ProductFiltersFragment : ViewStateBaseDialogFragment() {

    companion object {
        const val CONCRETE_FILTER = "CONCRETE_FILTER"
    }

    private lateinit var binding: FragmentFilterListBinding
    private val viewModel: ProductFiltersViewModel by viewModels()

    private val compositeDisposable = CompositeDisposable()

    private val onFilterClickSubject: PublishSubject<FilterUI> = PublishSubject.create()
    private val onFilterClearClickSubject: PublishSubject<FilterUI> = PublishSubject.create()

    private val productFiltersAdapter = ProductFiltersAdapter(
        onFilterClickSubject = onFilterClickSubject,
        onFilterClearClickSubject = onFilterClearClickSubject
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
        getArgs()
    }

    private fun getArgs() {
        with(ProductFiltersFragmentArgs.fromBundle(requireArguments())) {
            viewModel.setArgs(defaultFiltersBundle, categoryId)
        }
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup,
    ) = FragmentFilterListBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
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
        binding.incAppBar.tvTitle.text = resources.getString(R.string.products_filters_title)
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initFilterRecycler() {
        val space16 = resources.getDimension(R.dimen.space_16).toInt()
        val space12 = resources.getDimension(R.dimen.space_12).toInt()
        binding.rvFilters.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFilters.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.rvFilters.addMarginDecoration { rect, view, parent, state ->
            rect.left = space16
            rect.right = space16
            rect.top = space12
            rect.bottom = space12
        }
        binding.rvFilters.adapter = productFiltersAdapter
    }

    private fun initFilterPrice() {
        binding.rsPrice.addOnChangeListener { rangeSlider, _, _ ->
            binding.tvMinPrice.text = rangeSlider.values.first().toInt().toString()
            binding.tvMaxPrice.text = rangeSlider.values.last().toInt().toString()

            viewModel.customFilterBundle!!.filterPriceUI.minPrice =
                if (rangeSlider.values.first().toInt() == rangeSlider.valueFrom.toInt()) Int.MIN_VALUE
                else rangeSlider.values.first().toInt()

            viewModel.customFilterBundle!!.filterPriceUI.maxPrice =
                if (rangeSlider.values.last().toInt() == rangeSlider.valueTo.toInt()) Int.MAX_VALUE
                else rangeSlider.values.last().toInt()        }
    }

    private fun initBottomButtons() {
        binding.tvApply.setOnClickListener { sendFilterBundleBack() }
        binding.tvClear.setOnClickListener {
            viewModel.clearCustomFilterBundle()
            sendFilterBundleBack()
        }
    }

    private fun sendFilterBundleBack() {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            PaginatedProductsCatalogFragment.FILTER_BUNDLE,
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
        viewModel.viewStateLD.observe(viewLifecycleOwner) { fetchState ->
            when (fetchState) {
                is ViewState.Loading -> onStateLoading()
                is ViewState.Error -> onStateError(fetchState.errorMessage)
                is ViewState.Success -> onStateSuccess()
                is ViewState.Hide -> onStateHide()
            }
        }

        viewModel.filtersBundleUILD.observe(viewLifecycleOwner) { filterBundleUI ->
            fillFilterPrice(filterBundleUI.filterPriceUI)
            fillFilterList(filterBundleUI.filterUIList)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fillFilterList(filterList: List<FilterUI>) {
        productFiltersAdapter.filterList = filterList.toMutableList()
        productFiltersAdapter.notifyDataSetChanged()
    }

    private fun changeConcreteFilter(filterUI: FilterUI) {
        viewModel.changeConcreteFilter(filterUI)
    }

    private fun fillFilterPrice(filterPriceUI: FilterPriceUI) {
        with(binding) {
            rsPrice.valueFrom = filterPriceUI.minPrice.toFloat()
            rsPrice.valueTo = filterPriceUI.maxPrice.toFloat()

            val currentMinPrice =
                if (viewModel.customFilterBundle!!.filterPriceUI.minPrice == Int.MIN_VALUE) filterPriceUI.minPrice
                else viewModel.customFilterBundle!!.filterPriceUI.minPrice

            val currentMaxPrice =
                if (viewModel.customFilterBundle!!.filterPriceUI.maxPrice == Int.MAX_VALUE) filterPriceUI.maxPrice
                else viewModel.customFilterBundle!!.filterPriceUI.maxPrice

            rsPrice.values = listOf(
                currentMinPrice.toFloat(),
                currentMaxPrice.toFloat()
            )

            tvMaxPrice.text = currentMinPrice.toString()
            tvMaxPrice.text = currentMaxPrice.toString()
        }
    }

    override fun onStart() {
        super.onStart()

        onFilterClickSubject.subscribeBy { filter ->
            findNavController().navigate(ProductFiltersFragmentDirections.actionToConcreteFilterFragment(
                viewModel.categoryId!!,
                filter
            ))
        }.addTo(compositeDisposable)

        onFilterClearClickSubject.subscribeBy { filter ->
            viewModel.removeConcreteFilter(filter)
        }.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.dispose()
    }

}