package com.vodovoz.app.ui.components.fragment.allBrands

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentAllBrandsBinding
import com.vodovoz.app.ui.components.adapter.allBrandAdapter.AllBrandsAdapter
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.components.base.FetchStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.fragment.productsWithoutFilter.ProductsWithoutFiltersFragment
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.setScrollElevation
import com.vodovoz.app.ui.model.BrandUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject


class AllBrandsFragment : FetchStateBaseFragment() {

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
        viewModel.updateArgs(AllBrandsFragmentArgs.fromBundle(requireArguments()).brandIdList.toList())
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup,
    ) = FragmentAllBrandsBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
    }.root

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

    override fun initView() {
        initAppBar()
        initBrandRecycler()
        observeViewModel()
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

    override fun update() {
        viewModel.updateData()
    }

    private fun observeViewModel() {
        viewModel.fetchStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is FetchState.Loading -> onStateLoading()
                is FetchState.Error -> onStateError(state.errorMessage)
                is FetchState.Hide -> onStateHide()
                is FetchState.Success -> {
                    onStateSuccess()
                    fillBrandRecycler(state.data!!)
                }
            }
        }
    }

    private fun fillBrandRecycler(brandUIList: List<BrandUI>) {
        allBrandsAdapter.brandUIFullList = brandUIList
    }

    override fun onStart() {
        super.onStart()
        onBrandClickSubject.subscribeBy { brandId ->
            findNavController().navigate(
                AllBrandsFragmentDirections.actionAllBrandsFragmentToProductsWithoutFiltersFragment(
                    ProductsWithoutFiltersFragment.DataSource.Brand(brandId)
                )
            )
        }.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

}