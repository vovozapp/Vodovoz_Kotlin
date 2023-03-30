package com.vodovoz.app.ui.fragment.some_products_maybe_like

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentPaginatedMaybeLikeProductListBinding
import com.vodovoz.app.feature.productdetail.ProductDetailsFragmentDirections
import com.vodovoz.app.ui.adapter.GridProductsAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication

import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

@AndroidEntryPoint
class SomeProductsMaybeLikeFragment : ViewStateBaseFragment() {

    companion object {
        fun newInstance(
            onProductClickSubject: PublishSubject<Long>
        ) = SomeProductsMaybeLikeFragment().apply {
            this.onProductClickSubject = onProductClickSubject
        }
    }

    private val compositeDisposable = CompositeDisposable()

    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>> = PublishSubject.create()
    private lateinit var onProductClickSubject: PublishSubject<Long>
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>> = PublishSubject.create()

    private lateinit var binding: FragmentPaginatedMaybeLikeProductListBinding
    private val viewModel: SomeProductsMaybeLikeViewModel by viewModels()

    private val gridProductsAdapter: GridProductsAdapter by lazy {
        GridProductsAdapter(
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
                requireParentFragment().findNavController().navigate(ProductDetailsFragmentDirections.actionToPreOrderBS(
                    id, name, picture
                ))
            },
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.nextPage()
        subscribeSubjects()
    }

    private fun subscribeSubjects() {
        onChangeProductQuantitySubject.subscribeBy { pair ->
            viewModel.changeCart(pair.first, pair.second)
        }.addTo(compositeDisposable)
        onFavoriteClickSubject.subscribeBy { pair ->
            viewModel.changeFavoriteStatus(pair.first, pair.second)
        }.addTo(compositeDisposable)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentPaginatedMaybeLikeProductListBinding.inflate(
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
        binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProducts.adapter = gridProductsAdapter
        binding.tvNextPage.setOnClickListener { viewModel.nextPage() }
        binding.rvProducts.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    with(outRect) {
                        if (parent.getChildAdapterPosition(view) % 2 == 0) {
                            left = space
                            right = space/2
                        } else {
                            left = space/2
                            right = space
                        }
                        top = space
                        bottom = space
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
            gridProductsAdapter.productUiList = productUIList
            gridProductsAdapter.notifyDataSetChanged()

            if (viewModel.pageAmount == 1) {
                binding.tvNextPage.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}