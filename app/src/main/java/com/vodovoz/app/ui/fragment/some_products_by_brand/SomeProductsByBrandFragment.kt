package com.vodovoz.app.ui.fragment.some_products_by_brand

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentPaginatedBrandProductListBinding
import com.vodovoz.app.feature.all.orders.detail.OrderDetailsFragmentDirections
import com.vodovoz.app.ui.adapter.LinearProductsAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.subjects.PublishSubject

@AndroidEntryPoint
class SomeProductsByBrandFragment : ViewStateBaseFragment() {

    companion object {
        private const val BRAND_ID = "BRAND_ID"
        private const val PRODUCT_ID = "PRODUCT_ID"

        fun newInstance(
            productId: Long,
            brandId: Long,
            onProductClickSubject: PublishSubject<Long>
        ) = SomeProductsByBrandFragment().apply {
            this.onProductClickSubject = onProductClickSubject
            arguments = Bundle().also { args ->
                args.putLong(PRODUCT_ID, productId)
                args.putLong(BRAND_ID, brandId)
            }
        }
    }

    private lateinit var onProductClickSubject: PublishSubject<Long>

    private lateinit var binding: FragmentPaginatedBrandProductListBinding
    private val viewModel: SomeProductsByBrandViewModel by viewModels()

    private val linearProductAdapter: LinearProductsAdapter by lazy {
        LinearProductsAdapter(
            onProductClick = {
                onProductClickSubject.onNext(it)
            },
            onChangeFavoriteStatus = { productId, status ->
                viewModel.changeFavoriteStatus(productId, status)
            },
            onChangeCartQuantity = { productId, quantity ->
                viewModel.changeCart(productId, quantity)
            },
            onNotAvailableMore = {},
            onNotifyWhenBeAvailable = { id, name, picture ->
                requireParentFragment().findNavController().navigate(OrderDetailsFragmentDirections.actionToPreOrderBS(
                    id, name, picture
                ))
            },
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentPaginatedBrandProductListBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initProductRecycler()
        observeViewModel()
    }

    override fun update() {
        viewModel.updateData()
    }

    private fun initProductRecycler() {
        val space = resources.getDimension(R.dimen.space_16).toInt()
        binding.nextPage.setOnClickListener { viewModel.nextPage() }
        binding.brandProductRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.brandProductRecycler.adapter = linearProductAdapter
        binding.brandProductRecycler.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    with(outRect) {
                        top = space
                        bottom = space
                        right = space
                    }
                }
            }
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Hide -> onStateHide()
                is ViewState.Loading -> onStateLoading()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Success -> onStateSuccess()
            }
        }

        viewModel.productUIListLD.observe(viewLifecycleOwner) { productUIList ->
            linearProductAdapter.productUIList = productUIList
            linearProductAdapter.notifyDataSetChanged()

            if (viewModel.pageAmount == 1) {
                binding.nextPage.visibility = View.GONE
            }
        }
    }

}