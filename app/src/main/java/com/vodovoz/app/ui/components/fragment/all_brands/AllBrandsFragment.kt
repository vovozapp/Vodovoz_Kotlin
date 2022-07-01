package com.vodovoz.app.ui.components.fragment.all_brands

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentAllBrandsBinding
import com.vodovoz.app.ui.components.adapter.AllBrandsAdapter
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.ViewStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.diffUtils.BrandSliderDiffUtilCallback
import com.vodovoz.app.ui.components.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
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
        setHasOptionsMenu(true)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.all_brands_menu, menu)

        val searchItem = menu.findItem(R.id.actionSearch)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { allBrandsAdapter.filter(newText) }
                return false
            }
        })
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

    private fun initBrandRecycler() {
        binding.brandRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.brandRecycler.adapter = allBrandsAdapter
        binding.brandRecycler.setScrollElevation(binding.appBar)
        binding.brandRecycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
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
            val diffUtil = BrandSliderDiffUtilCallback(
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