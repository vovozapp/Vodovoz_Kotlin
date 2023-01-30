package com.vodovoz.app.ui.fragment.catalog

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentMainCatalogBinding
import com.vodovoz.app.ui.adapter.MainCatalogAdapter
import com.vodovoz.app.ui.base.BaseFragment
import com.vodovoz.app.ui.model.CategoryUI
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

@AndroidEntryPoint
class CatalogFragment : BaseFragment() {

    private val binding: FragmentMainCatalogBinding by viewBinding { FragmentMainCatalogBinding.bind(contentView) }
    private val viewModel: CatalogViewModel by viewModels()

    private val categoryClickSubject: PublishSubject<CategoryUI> = PublishSubject.create()
    private val mainCatalogAdapter = MainCatalogAdapter(
        categoryClickSubject = categoryClickSubject,
        nestingPosition = 0,
    )

    private val compositeDisposable = CompositeDisposable()

    override fun layout(): Int = R.layout.fragment_main_catalog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoad()
        observeOnCategoryClick()
    }

    override fun initView() {
        initCategoryRecycler()
        observeViewModel()
        initSearch()
    }

    override fun update() {
        viewModel.refresh()
    }

    private fun initCategoryRecycler() {
        binding.categoryRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.categoryRecycler.adapter = mainCatalogAdapter
        binding.categoryRecycler.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    binding.appBar.elevation =
                        if (binding.categoryRecycler.canScrollVertically(-1)) 16f
                        else 0f
                }
            }
        )
    }

    private fun initSearch() {
        binding.searchContainer.clSearchContainer.setOnClickListener {
            findNavController().navigate(CatalogFragmentDirections.actionToSearchFragment())
        }
        binding.searchContainer.etSearch.setOnFocusChangeListener { _, isFocusable ->
            if (isFocusable) {
                findNavController().navigate(CatalogFragmentDirections.actionToSearchFragment())
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect {catalogState ->

                    if (catalogState.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    mainCatalogAdapter.categoryUIList = catalogState.data.itemsList
                    mainCatalogAdapter.notifyDataSetChanged()

                    showError(catalogState.error)
                }
        }
    }

    private fun observeOnCategoryClick() {
        categoryClickSubject.subscribeBy { category ->
            findNavController().navigate(CatalogFragmentDirections.actionToPaginatedProductsCatalogFragment(category.id!!))
        }.addTo(compositeDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}