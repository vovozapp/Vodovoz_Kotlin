package com.vodovoz.app.ui.fragment.products_catalog

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentFixAmountProductsBinding
import com.vodovoz.app.ui.adapter.LinearProductsAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.setScrollElevation
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import java.io.Serializable

class ProductsCatalogFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentFixAmountProductsBinding
    private lateinit var viewModel: ProductsCatalogViewModel

    private val compositeDisposable = CompositeDisposable()

    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>> = PublishSubject.create()
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>> = PublishSubject.create()

    private val linearProductsAdapter =  LinearProductsAdapter(
        onProductClick = {
            findNavController().navigate(ProductsCatalogFragmentDirections.actionToProductDetailFragment(it))
        },
        onChangeFavoriteStatus = { productId, status ->
            viewModel.changeFavoriteStatus(productId, status)
        },
        onChangeCartQuantity = { productId, quantity ->
            viewModel.changeCart(productId, quantity)
        },
        onNotAvailableMore = {},
        onNotifyWhenBeAvailable = { id, name, picture ->
            findNavController().navigate(ProductsCatalogFragmentDirections.actionToPreOrderBS(
                id, name, picture
            ))
        },
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        getArgs()
        subscribeSubjects()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[ProductsCatalogViewModel::class.java]
    }

    private fun getArgs() {
        viewModel.updateArgs(ProductsCatalogFragmentArgs.fromBundle(requireArguments()).dataSource)
    }

    private fun subscribeSubjects() {
        onChangeProductQuantitySubject.subscribeBy { pair ->
            viewModel.changeCart(pair.first, pair.second)
        }.addTo(compositeDisposable)
        onFavoriteClickSubject.subscribeBy { pair ->
            viewModel.changeFavoriteStatus(pair.first, pair.second)
        }.addTo(compositeDisposable)
        onProductClickSubject.subscribeBy { productId ->
            findNavController().navigate(
                ProductsCatalogFragmentDirections.actionToProductDetailFragment(productId)
            )
        }.addTo(compositeDisposable)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentFixAmountProductsBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initProductRecycler()
        initAppBar()
        initSearch()
        observeViewModel()
    }

    private fun initAppBar() {
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initProductRecycler() {
        binding.productRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.productRecycler.adapter = linearProductsAdapter
        binding.productRecycler.setScrollElevation(binding.appBar)
        binding.productRecycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        val space = resources.getDimension(R.dimen.space_16).toInt()
        binding.productRecycler.addItemDecoration(
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

    private fun initSearch() {
        binding.searchContainer.clSearchContainer.setOnClickListener {
            findNavController().navigate(ProductsCatalogFragmentDirections.actionToSearchFragment())
        }
        binding.searchContainer.etSearch.setOnFocusChangeListener { _, isFocusable ->
            if (isFocusable) {
                findNavController().navigate(ProductsCatalogFragmentDirections.actionToSearchFragment())
            }
        }
    }

    override fun update() {
        viewModel.updateData()
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

        viewModel.productUIListLD.observe(viewLifecycleOwner) { productUIList ->
            linearProductsAdapter.productUIList = productUIList
            linearProductsAdapter.notifyDataSetChanged()
        }
    }

    sealed class DataSource : Serializable {
        class BannerProducts(val categoryId: Long) : DataSource()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}