package com.vodovoz.app.ui.fragment.some_products_maybe_like

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentPaginatedMaybeLikeProductListBinding
import com.vodovoz.app.ui.adapter.GridProductsAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

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
    private lateinit var viewModel: SomeProductsMaybeLikeViewModel

    private val gridProductsAdapter: GridProductsAdapter by lazy {
        GridProductsAdapter(
            onProductClickSubject = onProductClickSubject,
            onChangeProductQuantitySubject = onChangeProductQuantitySubject,
            onFavoriteClickSubject = onFavoriteClickSubject
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        subscribeSubjects()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[SomeProductsMaybeLikeViewModel::class.java]
        viewModel.nextPage()
    }

    private fun subscribeSubjects() {
        onChangeProductQuantitySubject.subscribeBy { pair ->
            viewModel.changeCart(pair)
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
        val space = resources.getDimension(R.dimen.primary_space).toInt()
        binding.brandProductRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.brandProductRecycler.adapter = gridProductsAdapter
        binding.nextPage.setOnClickListener { viewModel.nextPage() }
        binding.brandProductRecycler.addItemDecoration(
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
                binding.nextPage.visibility = View.GONE
                binding.divider.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}