package com.vodovoz.app.ui.components.fragment.paginatedMaybeLikeProductList

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentPaginatedMaybeLikeProductListBinding
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.components.base.FetchStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.adapter.gridProductAdapter.GridProductAdapter
import com.vodovoz.app.ui.components.adapter.pagingProductsAdapter.grid.GridMarginDecoration
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class PaginatedMaybeLikeProductListFragment : FetchStateBaseFragment() {

    private lateinit var binding: FragmentPaginatedMaybeLikeProductListBinding
    private lateinit var viewModel: PaginatedMaybeLikeProductListViewModel

    private val space: Int by lazy { resources.getDimension(R.dimen.primary_space).toInt() }

    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val gridProductAdapter = GridProductAdapter(onProductClickSubject)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[PaginatedMaybeLikeProductListViewModel::class.java]
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentPaginatedMaybeLikeProductListBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
    }.root

    override fun initView() {
        binding.brandProductRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.brandProductRecycler.adapter = gridProductAdapter
        binding.brandProductRecycler.addItemDecoration(GridMarginDecoration(space))
        binding.nextPage.setOnClickListener { viewModel.nextPage() }
        observeViewModel()
    }

    override fun update() {
        viewModel.updateData()
    }

    private fun observeViewModel() {
        viewModel.fetchStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is FetchState.Hide -> onStateHide()
                is FetchState.Loading -> onStateLoading()
                is FetchState.Error -> onStateError(state.errorMessage)
                is FetchState.Success -> {
                    onStateSuccess()
                    updateBrandProductRecycler(state.data!!)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateBrandProductRecycler(productUIList: List<ProductUI>) {
        gridProductAdapter.productUiList = productUIList
        gridProductAdapter.notifyDataSetChanged()
    }

}