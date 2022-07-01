package com.vodovoz.app.ui.components.fragment.some_products_by_brand

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentPaginatedBrandProductListBinding
import com.vodovoz.app.ui.components.adapter.LinearProductsAdapter
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.ViewStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

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

    private val compositeDisposable = CompositeDisposable()

    private lateinit var onProductClickSubject: PublishSubject<Long>
    private val onChangeProductQuantitySubject: PublishSubject<ProductUI> = PublishSubject.create()

    private lateinit var binding: FragmentPaginatedBrandProductListBinding
    private lateinit var viewModel: SomeProductsByBrandViewModel

    private val linearProductAdapter: LinearProductsAdapter by lazy {
        LinearProductsAdapter(
            onProductClickSubject = onProductClickSubject,
            onChangeProductQuantitySubject = onChangeProductQuantitySubject
        )
    }

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
        )[SomeProductsByBrandViewModel::class.java]
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
        val space = resources.getDimension(R.dimen.primary_space).toInt()
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
        }
    }

    override fun onStart() {
        super.onStart()

        onChangeProductQuantitySubject.subscribeBy { product ->
            viewModel.changeCart(product)
        }.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

}