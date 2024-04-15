package com.vodovoz.app.feature.filters.product

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentFilterListBinding
import com.vodovoz.app.feature.filters.product.adapter.OnFilterClearClickListener
import com.vodovoz.app.feature.filters.product.adapter.OnFilterClickListener
import com.vodovoz.app.feature.filters.product.adapter.ProductFiltersFlowAdapter
import com.vodovoz.app.feature.productlist.PaginatedProductsCatalogFragment
import com.vodovoz.app.ui.model.FilterPriceUI
import com.vodovoz.app.ui.model.FilterUI
import com.vodovoz.app.ui.model.custom.FiltersBundleUI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductFiltersFlowFragment : BaseFragment() {

    override fun layout(): Int {
        return R.layout.fragment_filter_list
    }

    companion object {
        const val CONCRETE_FILTER = "CONCRETE_FILTER"
    }

    private val binding: FragmentFilterListBinding by viewBinding {
        FragmentFilterListBinding.bind(
            contentView
        )
    }

    private val viewModel: ProductFiltersFlowViewModel by viewModels()

    private val onFilterClickListener = getFilterClickListener()
    private val onFilterClearClickListener = getFilterClearClickListener()

    private val productFiltersAdapter = ProductFiltersFlowAdapter(
        onFilterClickListener, onFilterClearClickListener
    ) {
        val filterBundleClear = filterBundle?.copy(
            filterUIList = mutableListOf()
        )
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            PaginatedProductsCatalogFragment.FILTER_BUNDLE,
            filterBundleClear
        )
        changeConcreteFilter(it)
    }

    private var filterBundle: FiltersBundleUI? = null
    private var defaultBundle: FiltersBundleUI? = null

    override fun initView() {
        initFilterRecycler()
        initFilterPrice()
        initAppBar()
        initBottomButtons()
        observeViewModel()
        observeResultLiveData()
        update()
    }

    override fun update() {
        viewModel.fetchAllFiltersByCategory()
    }

    private fun initAppBar() {
        binding.incAppBar.tvTitle.text = resources.getString(R.string.products_filters_title)
        binding.incAppBar.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initFilterRecycler() {
//        val space16 = resources.getDimension(R.dimen.space_16).toInt()
//        val space12 = resources.getDimension(R.dimen.space_10).toInt()
        binding.rvFilters.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFilters.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
//        binding.rvFilters.addMarginDecoration { rect, _, _, _ ->
//            rect.left = space16
//            rect.right = space16
//            rect.top = space12
//            rect.bottom = space12
//        }
        binding.rvFilters.adapter = productFiltersAdapter
    }

    private fun initFilterPrice() {
        binding.rsPrice.addOnChangeListener { rangeSlider, _, _ ->
            binding.tvMinPrice.text = rangeSlider.values.first().toInt().toString()
            binding.tvMaxPrice.text = rangeSlider.values.last().toInt().toString()

            if (filterBundle != null) {
                filterBundle!!.filterPriceUI.minPrice =
                    if (rangeSlider.values.first()
                            .toInt() == rangeSlider.valueFrom.toInt()
                    ) Int.MIN_VALUE
                    else rangeSlider.values.first().toInt()

                filterBundle!!.filterPriceUI.maxPrice =
                    if (rangeSlider.values.last()
                            .toInt() == rangeSlider.valueTo.toInt()
                    ) Int.MAX_VALUE
                    else rangeSlider.values.last().toInt()
            }
        }
    }

    private fun initBottomButtons() {
        binding.tvApply.setOnClickListener { sendFilterBundleBack() }
        binding.tvClear.setOnClickListener {
            filterBundle?.let { noNullFilterBundle ->
                noNullFilterBundle.filterPriceUI.maxPrice = Int.MAX_VALUE
                noNullFilterBundle.filterPriceUI.minPrice = Int.MIN_VALUE
                noNullFilterBundle.filterUIList.clear()
            }
            sendFilterBundleBack()
        }
    }

    private fun sendFilterBundleBack() {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            PaginatedProductsCatalogFragment.FILTER_BUNDLE,
            filterBundle
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
        lifecycleScope.launch {
            viewModel.observeUiState().collect { state ->
                if (state.loadingPage) {
                    showLoader()
                } else {
                    hideLoader()
                }

                filterBundle = state.data.filterBundle
                defaultBundle = state.data.defaultBundle
                if (defaultBundle != null) {
                    fillFilterPrice(defaultBundle!!.filterPriceUI)
                    val filterList = defaultBundle?.filterUIList ?: mutableListOf()
                    fillFilterList(filterList)
                }

                showError(state.error)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fillFilterList(filterList: List<FilterUI>) {
        productFiltersAdapter.submitList(filterList)
        productFiltersAdapter.notifyDataSetChanged()
    }

    private fun changeConcreteFilter(filterUI: FilterUI) {
        viewModel.changeConcreteFilter(filterUI, filterBundle, defaultBundle)
    }

    private fun fillFilterPrice(filterPriceUI: FilterPriceUI) {
        with(binding) {
            rsPrice.valueFrom = filterPriceUI.minPrice.toFloat()
            rsPrice.valueTo = filterPriceUI.maxPrice.toFloat()

            var currentMinPrice = 0
            var currentMaxPrice = Int.MAX_VALUE
            if (filterBundle != null) {
                currentMinPrice =
                    if (filterBundle!!.filterPriceUI.minPrice == Int.MIN_VALUE) filterPriceUI.minPrice
                    else filterBundle!!.filterPriceUI.minPrice

                currentMaxPrice =
                    if (filterBundle!!.filterPriceUI.maxPrice == Int.MAX_VALUE) filterPriceUI.maxPrice
                    else filterBundle!!.filterPriceUI.maxPrice
            }

            rsPrice.values = listOf(
                currentMinPrice.toFloat(),
                currentMaxPrice.toFloat()
            )

            tvMaxPrice.text = currentMinPrice.toString()
            tvMaxPrice.text = currentMaxPrice.toString()
        }
    }

    private fun getFilterClearClickListener(): OnFilterClearClickListener {
        return object : OnFilterClearClickListener {
            override fun onFilterClearClick(filter: FilterUI) {
                filterBundle?.filterUIList?.removeAll { it.code == filter.code }
            }
        }
    }

    private fun getFilterClickListener(): OnFilterClickListener {
        return object : OnFilterClickListener {
            override fun onClickFilter(filter: FilterUI) {
                val categoryId =
                    ProductFiltersFlowFragmentArgs.fromBundle(requireArguments()).categoryId
                findNavController().navigate(
                    ProductFiltersFlowFragmentDirections.actionToConcreteFilterFragment(
                        categoryId,
                        filter
                    )
                )
            }
        }
    }
}