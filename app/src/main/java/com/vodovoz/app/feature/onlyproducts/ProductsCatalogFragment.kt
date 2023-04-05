package com.vodovoz.app.feature.onlyproducts

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.databinding.FragmentFixAmountProductsBinding
import com.vodovoz.app.feature.catalog.CatalogFragmentDirections
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.adapter.LinearProductsAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.setScrollElevation
import com.vodovoz.app.util.extensions.snack
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import java.io.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class ProductsCatalogFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_fix_amount_products

    private val binding: FragmentFixAmountProductsBinding by viewBinding {
        FragmentFixAmountProductsBinding.bind(
            contentView
        )
    }

    private val viewModel: OnlyProductsViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    private val onlyProductsController by lazy {
        OnlyProductsController(
            viewModel,
            cartManager,
            likeManager,
            getProductsClickListener(),
            requireContext(),
            ratingProductManager
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.firstLoadSorted()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onlyProductsController.bind(binding.productRecycler, binding.refreshContainer)
        bindErrorRefresh { viewModel.refreshSorted() }
        observeUiState()
        initBackButton()
        initSearch()
    }

    private fun initSearch() {
        initSearchToolbar(
            { findNavController().navigate(ProductsCatalogFragmentDirections.actionToSearchFragment()) },
            { findNavController().navigate(ProductsCatalogFragmentDirections.actionToSearchFragment()) },
            true
        )
    }

    private fun initBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        )
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect { state ->

                    if (state.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    val data = state.data
                    if (state.bottomItem != null) {
                        onlyProductsController.submitList(data.itemsList + state.bottomItem)
                    } else {
                        onlyProductsController.submitList(data.itemsList)
                    }

                    if (state.error !is ErrorState.Empty) {
                        showError(state.error)
                    }

                }
        }
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                findNavController().navigate(
                    ProductsCatalogFragmentDirections.actionToProductDetailFragment(
                        id
                    )
                )
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                findNavController().navigate(
                    ProductsCatalogFragmentDirections.actionToPreOrderBS(
                        id,
                        name,
                        detailPicture
                    )
                )
            }

            override fun onChangeProductQuantity(id: Long, cartQuantity: Int, oldQuantity: Int) {
                viewModel.changeCart(id, cartQuantity, oldQuantity)
            }

            override fun onFavoriteClick(id: Long, isFavorite: Boolean) {
                viewModel.changeFavoriteStatus(id, isFavorite)
            }

            override fun onChangeRating(id: Long, rating: Float, oldRating: Float) {
                viewModel.changeRating(id, rating, oldRating)
            }

        }
    }

    sealed class DataSource : Serializable {
        class BannerProducts(val categoryId: Long) : DataSource()
    }

}
/*
@AndroidEntryPoint
class ProductsCatalogFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentFixAmountProductsBinding
    private val viewModel: ProductsCatalogViewModel by viewModels()

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
        getArgs()
        subscribeSubjects()
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
        binding.imgBack.setOnClickListener {
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
        binding.incSearch.clSearchContainer.setOnClickListener {
            findNavController().navigate(ProductsCatalogFragmentDirections.actionToSearchFragment())
        }
        binding.incSearch.etSearch.setOnFocusChangeListener { _, isFocusable ->
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

}*/
