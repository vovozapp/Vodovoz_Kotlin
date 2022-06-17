package com.vodovoz.app.ui.components.fragment.catalog

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.FragmentMainCatalogBinding
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.components.base.FetchStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.adapter.categoryAdapter.CatalogCategoryAdapter
import com.vodovoz.app.ui.model.CategoryUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class CatalogFragment : FetchStateBaseFragment() {

    private lateinit var binding: FragmentMainCatalogBinding
    private lateinit var viewModel: CatalogViewModel

    private val categoryClickSubject: PublishSubject<CategoryUI> = PublishSubject.create()
    private val catalogCategoryAdapter = CatalogCategoryAdapter(
        categoryClickSubject = categoryClickSubject,
        nestingPosition = 0,
    )

    private val compositeDisposable = CompositeDisposable()

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentMainCatalogBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
    }.root

    override fun onStart() {
        super.onStart()
        if (viewModel.lastFetchState is FetchState.Error) viewModel.updateData()
    }

    override fun initView() {
        initViewModel()
        initCategoryRecycler()
        observeViewModel()
        observeOnCategoryClick()
    }

    override fun update() {
        viewModel.updateData()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[CatalogViewModel::class.java]
    }

    private fun initCategoryRecycler() {
        binding.categoryRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.categoryRecycler.adapter = catalogCategoryAdapter
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

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        viewModel.fetchStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is FetchState.Error -> onStateError(state.errorMessage)
                is FetchState.Loading -> onStateLoading()
                is FetchState.Success -> {
                    onStateSuccess()
                    catalogCategoryAdapter.categoryUIList = state.data!!
                    catalogCategoryAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun observeOnCategoryClick() {
        categoryClickSubject.subscribeBy { category ->
            findNavController().navigate(CatalogFragmentDirections.actionCatalogFragmentToProductsFragment(category.id!!))
        }.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}