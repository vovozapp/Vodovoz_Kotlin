package com.vodovoz.app.ui.fragment.product_filters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentFilterListBinding
import com.vodovoz.app.ui.adapter.ProductFiltersAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseDialogFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.fragment.paginated_products_catalog.PaginatedProductsCatalogFragment
import com.vodovoz.app.ui.model.FilterPriceUI
import com.vodovoz.app.ui.model.FilterUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject


class ProductFiltersFragment : ViewStateBaseDialogFragment() {

    companion object {
        const val CONCRETE_FILTER = "CONCRETE_FILTER"
    }

    private lateinit var binding: FragmentFilterListBinding
    private lateinit var viewModel: ProductFiltersViewModel

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
        initViewModel()
        getArgs()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[ProductFiltersViewModel::class.java]
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
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.let { noNullActionBar ->
            noNullActionBar.setDisplayHomeAsUpEnabled(true)
            noNullActionBar.setDisplayShowHomeEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initFilterRecycler() {
        binding.filterRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.filterRecycler.adapter = productFiltersAdapter
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
        compositeDisposable.clear()
    }

}