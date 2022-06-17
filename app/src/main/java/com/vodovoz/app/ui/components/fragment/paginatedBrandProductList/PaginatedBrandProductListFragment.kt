package com.vodovoz.app.ui.components.fragment.paginatedBrandProductList

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentPaginatedBrandProductListBinding
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.components.base.FetchStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.adapter.linearProductAdapter.LinearProductAdapter
import com.vodovoz.app.ui.components.adapter.pagingProductsAdapter.list.ListMarginDecoration
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.subjects.PublishSubject

class PaginatedBrandProductListFragment : FetchStateBaseFragment() {

    companion object {
        private const val BRAND_ID = "BRAND_ID"
        private const val PRODUCT_ID = "PRODUCT_ID"

        fun newInstance(
            productId: Long,
            brandId: Long
        ) = PaginatedBrandProductListFragment().apply {
            arguments = Bundle().also { args ->
                args.putLong(PRODUCT_ID, productId)
                args.putLong(BRAND_ID, brandId)
            }
        }
    }

    private lateinit var binding: FragmentPaginatedBrandProductListBinding
    private lateinit var viewModel: PaginatedBrandProductListViewModel

    private val space: Int by lazy { resources.getDimension(R.dimen.primary_space).toInt() }

    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val linearProductAdapter = LinearProductAdapter(onProductClickSubject)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        getArgs()
    }

    private fun getArgs() {
        arguments?.let { args ->
            viewModel.updateArgs(
                productId = args.getLong(PRODUCT_ID),
                brandId = args.getLong(BRAND_ID)
            )
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[PaginatedBrandProductListViewModel::class.java]
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentPaginatedBrandProductListBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
    }.root

    override fun initView() {
        binding.brandProductRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.brandProductRecycler.adapter = linearProductAdapter
        binding.brandProductRecycler.addItemDecoration(ListMarginDecoration(space))
        binding.nextPage.setOnClickListener {
            viewModel.nextPage()
        }
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
        linearProductAdapter.productUiList = productUIList
        linearProductAdapter.notifyDataSetChanged()
    }

}