package com.vodovoz.app.ui.fragment.all_brands

import android.os.Bundle
import android.view.*
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentAllBrandsBinding
import com.vodovoz.app.ui.adapter.AllBrandsAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.diffUtils.BrandDiffUtilCallback
import com.vodovoz.app.ui.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.setScrollElevation
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject


class AllBrandsFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentAllBrandsBinding
    private lateinit var viewModel: AllBrandsViewModel

    private val compositeDisposable = CompositeDisposable()
    private val onBrandClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val allBrandsAdapter = AllBrandsAdapter(onBrandClickSubject)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        getArgs()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[AllBrandsViewModel::class.java]
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
        compositeDisposable.clear()
    }

}