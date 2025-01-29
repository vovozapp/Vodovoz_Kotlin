package com.vodovoz.app.feature.filters.product

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.compose.runtime.mutableStateOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.State
import com.vodovoz.app.databinding.FragmentFilterListBinding
import com.vodovoz.app.feature.filters.product.adapter.OnFilterClearClickListener
import com.vodovoz.app.feature.filters.product.adapter.OnFilterClickListener
import com.vodovoz.app.feature.filters.product.adapter.ProductFiltersFlowAdapter
import com.vodovoz.app.feature.productlist.PaginatedProductsCatalogFragment
import com.vodovoz.app.ui.model.FilterPriceUI
import com.vodovoz.app.ui.model.FilterUI
import com.vodovoz.app.ui.model.custom.FiltersBundleUI
import com.vodovoz.app.util.extensions.preDraw
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
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

    private val prices = MutableStateFlow<List<Int>>(listOf())
    private val collectedPrices = prices.asStateFlow()

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
        binding.incAppBar.imgBack.visibility = View.INVISIBLE
        binding.incAppBar.tvTitle.text = resources.getString(R.string.products_filters_title)
        //binding.incAppBar.imgBack.setOnClickListener {
        //    findNavController().popBackStack()
        //}
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

        binding.rvFilters.preDraw {
            viewModel.recyclerReady(it)
        }
    }

    private fun initFilterPrice(filterPriceUI: FilterPriceUI) {
        binding.rsPrice.addOnChangeListener { rangeSlider, _, _ ->
            if (rangeSlider.values.first() != rangeSlider.values.last()){
                when (binding.etMinPrice.text.toString().isNotEmpty()){
                    true ->{
                        if (rangeSlider.values.first().toInt() != binding.etMinPrice.text.toString().toInt())
                            binding.etMinPrice.setText(rangeSlider.values.first().toInt().toString())

                        if (rangeSlider.values.first().toInt() == filterPriceUI.minPrice){
                            binding.etMinPrice.setText("")
                            binding.etMinPrice.setHint(rangeSlider.values.first().toInt().toString())
                        }
                    }
                    false ->
                        if (rangeSlider.values.first().toInt() == filterPriceUI.minPrice){
                            binding.etMinPrice.setText("")
                            binding.etMinPrice.setHint(rangeSlider.values.first().toInt().toString())
                        } else binding.etMinPrice.setText(rangeSlider.values.first().toInt().toString())
                }

                when (binding.etMaxPrice.text.toString().isNotEmpty()){
                    true -> {
                        if (rangeSlider.values.last().toInt() != binding.etMaxPrice.text.toString().toInt())
                            binding.etMaxPrice.setText(rangeSlider.values.last().toInt().toString())

                        if (rangeSlider.values.last().toInt() == filterPriceUI.maxPrice){
                            binding.etMaxPrice.setText("")
                            binding.etMaxPrice.setHint(rangeSlider.values.last().toInt().toString())
                        }
                    }
                    false -> {
                        if (rangeSlider.values.last().toInt() == filterPriceUI.maxPrice){
                            binding.etMaxPrice.setText("")
                            binding.etMaxPrice.setHint(rangeSlider.values.last().toInt().toString())
                        } else binding.etMaxPrice.setText(rangeSlider.values.last().toInt().toString())
                    }
                }
            }

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
        binding.btnApply.setOnClickListener { sendFilterBundleBack() }
        binding.incAppBar.imgClear.setOnClickListener {
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
                if (filter == null) return@observe
                changeConcreteFilter(filter)
                findNavController().currentBackStackEntry?.savedStateHandle?.set(
                    CONCRETE_FILTER,
                    null
                )
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    CONCRETE_FILTER,
                    null
                )
            }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.observeUiState().collect { state ->
                showLoaderWithBg(state.loadingPage)

                filterBundle = state.data.filterBundle
                defaultBundle = state.data.defaultBundle

                binding.rvFilters.isVisible = defaultBundle != null
                if (defaultBundle != null) {
                    val filterList = defaultBundle?.filterUIList ?: mutableListOf()
                    fillFilterList(filterList)
                    initFilterPrice(defaultBundle!!.filterPriceUI)
                    fillFilterPrice(defaultBundle!!.filterPriceUI)
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

            etMinPrice.setHint(currentMinPrice.toString())
            etMaxPrice.setHint(currentMaxPrice.toString())
            initPricesTextWatchers(filterPriceUI)
        }
    }

    private fun setSliderPrices(filterPriceUI: FilterPriceUI){
        lifecycleScope.launch {
            collectedPrices.collect{ collectedPrices ->
                if (collectedPrices.isNotEmpty()){
                    var prices = collectedPrices
                    if (prices.first() > filterPriceUI.maxPrice)
                        prices = listOf(filterPriceUI.maxPrice, filterPriceUI.maxPrice)

                    if (prices.last() > filterPriceUI.maxPrice)
                        prices = listOf(prices.first(), filterPriceUI.maxPrice)

                    var rsPriceValues = listOf<Float>()
                    if (prices.first() >= filterPriceUI.minPrice && prices.first() <= prices.last()){
                        rsPriceValues = listOf(prices.first().toFloat(), prices.last().toFloat())
                        binding.rsPrice.values = rsPriceValues
                    }
                }
            }
        }
    }

    private fun initPricesTextWatchers(filterPriceUI: FilterPriceUI){
        setSliderPrices(filterPriceUI)
        with(binding){
            etMinPrice.doOnTextChanged { text, _, _, _ ->
                val currentMaxPriceStr = etMaxPrice.text.toString()
                var currentMaxPrice = filterPriceUI.maxPrice
                if (currentMaxPriceStr.isNotEmpty())
                    currentMaxPrice = currentMaxPriceStr.toInt()
                if (!text.isNullOrEmpty()) {
                    prices.value = listOf(text.toString().toInt(), currentMaxPrice)
                }
                else prices.value = listOf(filterPriceUI.minPrice, currentMaxPrice)
            }

            etMaxPrice.doOnTextChanged { text, _, _, _ ->
                val currentMinPriceStr = etMinPrice.text.toString()
                var currentMinPrice = filterPriceUI.minPrice
                if (currentMinPriceStr.isNotEmpty())
                    currentMinPrice = currentMinPriceStr.toInt()
                if (!text.isNullOrEmpty()) {
                    prices.value = listOf(currentMinPrice, text.toString().toInt())
                }
                else prices.value = listOf(currentMinPrice, filterPriceUI.maxPrice)
            }
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