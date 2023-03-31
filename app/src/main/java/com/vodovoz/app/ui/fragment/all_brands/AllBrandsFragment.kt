package com.vodovoz.app.ui.fragment.all_brands

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.databinding.FragmentAllBrandsBinding
import com.vodovoz.app.feature.all.AllAdapterController
import com.vodovoz.app.feature.all.AllClickListener
import com.vodovoz.app.feature.all.brands.AllBrandsFlowViewModel
import com.vodovoz.app.ui.adapter.AllBrandsAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.diffUtils.BrandDiffUtilCallback
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.setScrollElevation
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

@AndroidEntryPoint
class AllBrandsFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_all_brands

    private val binding: FragmentAllBrandsBinding by viewBinding {
        FragmentAllBrandsBinding.bind(
            contentView
        )
    }

    private val viewModel: AllBrandsFlowViewModel by viewModels()

    private val allAdapterController by lazy {
        AllAdapterController(getAllClickListener())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.firstLoadSorted()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allAdapterController.bind(binding.rvBrands)

        observeUiState()
        initAppBar()
    }

    private fun initAppBar() {
        binding.incAppBar.tvTitle.text = resources.getString(R.string.all_brands_title)
        binding.incAppBar.imgBack.setOnClickListener {
            when(binding.incAppBar.llSearchContainer.visibility == View.VISIBLE) {
                true -> {
                    binding.incAppBar.llTitleContainer.visibility = View.VISIBLE
                    binding.incAppBar.llSearchContainer.visibility = View.GONE
                    binding.incAppBar.etSearch.setText("")
                }
                false -> findNavController().popBackStack()
            }
        }
        binding.incAppBar.imgSearch.setOnClickListener {
            binding.incAppBar.llTitleContainer.visibility = View.GONE
            binding.incAppBar.llSearchContainer.visibility = View.VISIBLE
        }
        binding.incAppBar.imgClear.setOnClickListener {
            binding.incAppBar.etSearch.setText("")
            binding.incAppBar.llTitleContainer.visibility = View.VISIBLE
            binding.incAppBar.llSearchContainer.visibility = View.GONE
        }
        binding.incAppBar.etSearch.doAfterTextChanged { query ->
            when(query.toString().isEmpty()) {
                true -> binding.incAppBar.imgClear.visibility = View.GONE
                false -> binding.incAppBar.imgClear.visibility = View.VISIBLE
            }

            viewModel.filterByQuery(query.toString())
        }
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect { state ->

                    if (state.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    allAdapterController.submitList(state.data.filteredItems)

                    if (state.error !is ErrorState.Empty) {
                        showError(state.error)
                    }

                }
        }
    }

    private fun getAllClickListener(): AllClickListener {
        return object : AllClickListener {
            override fun onPromotionClick(id: Long) {}

            override fun onBrandClick(id: Long) {
                findNavController().navigate(
                    AllBrandsFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                        PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Brand(brandId = id)
                    )
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (binding.incAppBar.etSearch.text.isNullOrBlank().not()) {
            binding.incAppBar.llTitleContainer.visibility = View.GONE
            binding.incAppBar.llSearchContainer.visibility = View.VISIBLE
        }
    }

}
/*
@AndroidEntryPoint
class AllBrandsFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentAllBrandsBinding
    private val viewModel: AllBrandsViewModel by viewModels()

    private val compositeDisposable = CompositeDisposable()
    private val onBrandClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val allBrandsAdapter = AllBrandsAdapter(onBrandClickSubject)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    private fun getArgs() {
        viewModel.updateArgs(AllBrandsFragmentArgs.fromBundle(requireArguments()).brandIdList?.toList() ?: listOf())
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup,
    ) = FragmentAllBrandsBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initAppBar()
        initBrandRecycler()
        observeViewModel()
    }

    private fun initAppBar() {
        binding.incAppBar.tvTitle.text = resources.getString(R.string.all_brands_title)
        binding.incAppBar.imgBack.setOnClickListener {
            when(binding.incAppBar.llSearchContainer.visibility == View.VISIBLE) {
                true -> {
                    binding.incAppBar.llTitleContainer.visibility = View.VISIBLE
                    binding.incAppBar.llSearchContainer.visibility = View.GONE
                    binding.incAppBar.etSearch.setText("")
                }
                false -> findNavController().popBackStack()
            }
        }
        binding.incAppBar.imgSearch.setOnClickListener {
            binding.incAppBar.llTitleContainer.visibility = View.GONE
            binding.incAppBar.llSearchContainer.visibility = View.VISIBLE
        }
        binding.incAppBar.imgClear.setOnClickListener { binding.incAppBar.etSearch.setText("") }
        binding.incAppBar.etSearch.doAfterTextChanged { query ->
            when(query.toString().isEmpty()) {
                true -> binding.incAppBar.imgClear.visibility = View.GONE
                false -> binding.incAppBar.imgClear.visibility = View.VISIBLE
            }

            allBrandsAdapter.filter(query.toString())
        }
    }

    private fun initBrandRecycler() {
        binding.rvBrands.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBrands.adapter = allBrandsAdapter
        binding.rvBrands.setScrollElevation(binding.incAppBar.root)
        binding.rvBrands.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }

    override fun update() { viewModel.updateData() }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Loading -> onStateLoading()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Hide -> onStateHide()
                is ViewState.Success -> onStateSuccess()
            }
        }

        viewModel.brandUIListLD.observe(viewLifecycleOwner) { brandUIList ->
            val diffUtil = BrandDiffUtilCallback(
                oldList = allBrandsAdapter.brandUIFullList,
                newList = brandUIList
            )

            DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                allBrandsAdapter.brandUIFullList = brandUIList
                diffResult.dispatchUpdatesTo(allBrandsAdapter)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        onBrandClickSubject.subscribeBy { brandId ->
            findNavController().navigate(
                AllBrandsFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Brand(brandId = brandId)
                )
            )
        }.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.dispose()
    }

}*/
