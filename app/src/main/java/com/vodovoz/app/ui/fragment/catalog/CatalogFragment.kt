package com.vodovoz.app.ui.fragment.catalog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.FragmentMainCatalogBinding
import com.vodovoz.app.ui.adapter.MainCatalogAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.model.CategoryUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class CatalogFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentMainCatalogBinding
    private lateinit var viewModel: CatalogViewModel

    private val categoryClickSubject: PublishSubject<CategoryUI> = PublishSubject.create()
    private val mainCatalogAdapter = MainCatalogAdapter(
        categoryClickSubject = categoryClickSubject,
        nestingPosition = 0,
    )

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        observeOnCategoryClick()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[CatalogViewModel::class.java]
        viewModel.updateData()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentMainCatalogBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initViewModel()
        initCategoryRecycler()
        observeViewModel()
        initSearch()
    }

    override fun update() {
        viewModel.updateData()
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
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Loading -> onStateLoading()
                is ViewState.Hide -> onStateHide()
                is ViewState.Success -> onStateSuccess()
            }
        }

        viewModel.categoryUIListLD.observe(viewLifecycleOwner) { categoryUIList ->
            mainCatalogAdapter.categoryUIList = categoryUIList
            mainCatalogAdapter.notifyDataSetChanged()
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