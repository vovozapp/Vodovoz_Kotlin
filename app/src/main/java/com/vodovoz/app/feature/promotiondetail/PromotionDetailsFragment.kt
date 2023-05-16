package com.vodovoz.app.feature.promotiondetail

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.databinding.FragmentPromotionDetailFlowBinding
import com.vodovoz.app.feature.favorite.bestforyouadapter.BestForYouController
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.model.PromotionDetailUI
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PromotionDetailsFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_promotion_detail_flow

    private val binding: FragmentPromotionDetailFlowBinding by viewBinding {
        FragmentPromotionDetailFlowBinding.bind(
            contentView
        )
    }

    private val viewModel: PromotionDetailFlowViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    private val bestForYouController by lazy { BestForYouController(cartManager, likeManager, getProductsShowClickListener(), getProductsClickListener()) }

    private val productsController by lazy {
        PromotionDetailFlowController(
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

        productsController.bind(binding.productRecycler, null)
        bestForYouController.bind(binding.fcvProductSliderRv)

        observeUiState()
        initBackButton()
        bindErrorRefresh {
            viewModel.refreshSorted()
        }
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
                        binding.timeLeftContainer.isVisible = true
                        binding.customerCategoryCard.isVisible = true
                        binding.rootView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_white))
                    }

                    bindHeader(state.data.items)

                    if (state.data.items?.forYouCategoryDetailUI != null) {
                        val homeProducts = HomeProducts(
                            1,
                            listOf(state.data.items.forYouCategoryDetailUI),
                            ProductsSliderConfig(
                                containShowAllButton = false,
                                largeTitle = true
                            ),
                            HomeProducts.DISCOUNT,
                            prodList = state.data.items.forYouCategoryDetailUI.productUIList
                        )
                        bestForYouController.submitList(listOf(homeProducts))
                    }

                    when(state.data.items?.promotionCategoryDetailUI) {
                        null -> {
                            binding.promotionProductsTitle.visibility = View.GONE
                            binding.productRecycler.visibility = View.GONE
                        }
                        else -> {
                            binding.promotionProductsTitle.visibility = View.VISIBLE
                            binding.productRecycler.visibility = View.VISIBLE
                            productsController.submitList(state.data.items.promotionCategoryDetailUI.productUIList)
                        }
                    }

                    showError(state.error)

                }
        }
    }

    private fun bindHeader(promotionDetailUI: PromotionDetailUI?) {
        if (promotionDetailUI == null) return

        initToolbar(titleText = promotionDetailUI.name)
        binding.customerCategoryCard.setCardBackgroundColor(Color.parseColor(promotionDetailUI.statusColor))
        binding.customerCategory.text = promotionDetailUI.status
        binding.timeLeft.text = promotionDetailUI.timeLeft
        binding.detail.text = HtmlCompat.fromHtml(promotionDetailUI.detailText, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        Glide.with(requireContext())
            .load(promotionDetailUI.detailPicture)
            .into(binding.image)
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                findNavController().navigate(
                    PromotionDetailsFragmentDirections.actionToProductDetailFragment(
                        id
                    )
                )
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                findNavController().navigate(
                    PromotionDetailsFragmentDirections.actionToPreOrderBS(
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

    private fun getProductsShowClickListener() : ProductsShowAllListener {
        return object : ProductsShowAllListener {
            override fun showAllDiscountProducts(id: Long) {}
            override fun showAllTopProducts(id: Long) {}
            override fun showAllNoveltiesProducts(id: Long) {}
            override fun showAllBottomProducts(id: Long) {}
        }
    }

}
/*
@AndroidEntryPoint
class PromotionDetailsFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentPromotionDetailBinding
    private val viewModel: PromotionDetailsViewModel by viewModels()

    private val bestForYouProductsSliderFragment: ProductsSliderFragment by lazy {
        ProductsSliderFragment.newInstance(ProductsSliderConfig(
            containShowAllButton = false
        ), true) }

    private val compositeDisposable = CompositeDisposable()

    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>> = PublishSubject.create()
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>> = PublishSubject.create()

    private val linearProductAdapter = LinearProductsAdapter(
        onProductClick = {
            findNavController().navigate(PromotionDetailsFragmentDirections.actionToProductDetailFragment(it))
        },
        onChangeFavoriteStatus = { productId, status ->
            viewModel.changeFavoriteStatus(productId, status)
        },
        onChangeCartQuantity = { productId, quantity ->
            viewModel.changeCart(productId, quantity)
        },
        onNotAvailableMore = {},
        onNotifyWhenBeAvailable = { id, name, picture ->
            findNavController().navigate(PromotionDetailsFragmentDirections.actionToPreOrderBS(
                id, name, picture
            ))
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
        subscribeSubjects()
    }

    private fun subscribeSubjects() {
        onProductClickSubject.subscribeBy { productId ->
            findNavController().navigate(PromotionDetailsFragmentDirections.actionToProductDetailFragment(productId))
        }.addTo(compositeDisposable)
        onChangeProductQuantitySubject.subscribeBy { pair ->
            viewModel.changeCart(pair.first, pair.second)
        }.addTo(compositeDisposable)
        onFavoriteClickSubject.subscribeBy { pair ->
            viewModel.changeFavoriteStatus(pair.first, pair.second)
        }.addTo(compositeDisposable)
    }

    private fun getArgs() {
        viewModel.updateArgs(PromotionDetailsFragmentArgs.fromBundle(requireArguments()).promotionId)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentPromotionDetailBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initActionBar()
        initPromotionProductRecycler()
        initBestForYouProductsSliderFragment()
        observeViewModel()
    }

    private fun initActionBar() {
        binding.incAppBar.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private val linearDividerItemDecoration: DividerItemDecoration by lazy {
        DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    }

    private fun initPromotionProductRecycler() {
        binding.productRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.productRecycler.adapter = linearProductAdapter
        binding.contentContainer.setScrollElevation(binding.incAppBar.root)
        binding.productRecycler.addItemDecoration(ListMarginDecoration(
            resources.getDimension(R.dimen.space_16).toInt()
        ))
        binding.productRecycler.addItemDecoration(linearDividerItemDecoration)
    }

    private fun initBestForYouProductsSliderFragment() {
        bestForYouProductsSliderFragment.initCallbacks(
            iOnProductClick = { productId -> onProductClickSubject.onNext(productId) },
            iOnChangeProductQuantity = { pair -> onChangeProductQuantitySubject.onNext(pair) },
            iOnFavoriteClick = { pair -> onFavoriteClickSubject.onNext(pair) },
            iOnShowAllProductsClick = {
                findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Discount()
                ))
            },
            onNotAvailableMore = {},
            onNotifyWhenBeAvailable = { id, name, picture ->
                when(viewModel.isAlreadyLogin()) {
                    true -> findNavController().navigate(PromotionDetailsFragmentDirections.actionToPreOrderBS(id, name, picture))
                    false -> findNavController().navigate(PromotionDetailsFragmentDirections.actionToProfileFragment())
                }
            }
        )

        childFragmentManager.beginTransaction().replace(
            R.id.fcvProductSlider,
            bestForYouProductsSliderFragment
        ).commit()

    }

    override fun update() {
        viewModel.updateData()
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Loading -> onStateLoading()
                is ViewState.Hide -> onStateHide()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Success -> onStateSuccess()
            }
        }

        viewModel.promotionDetailUILD.observe(viewLifecycleOwner) { promotionDetailUI ->
            fillView(promotionDetailUI)
        }

        viewModel.errorLD.observe(viewLifecycleOwner) { errorMessage ->
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun fillView(promotionDetailUI: PromotionDetailUI) {
        fillPromotionHeader(promotionDetailUI)
        fillPromotionProductRecycler(promotionDetailUI.promotionCategoryDetailUI)
        fillForYouProductSlider(promotionDetailUI.forYouCategoryDetailUI)
    }

    private fun fillPromotionHeader(promotionDetailUI: PromotionDetailUI) {
        binding.incAppBar.tvTitle.text = promotionDetailUI.name
        binding.customerCategoryCard.setCardBackgroundColor(Color.parseColor(promotionDetailUI.statusColor))
        binding.customerCategory.text = promotionDetailUI.status
        binding.timeLeft.text = promotionDetailUI.timeLeft
        binding.detail.text = HtmlCompat.fromHtml(promotionDetailUI.detailText, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        Glide.with(requireContext())
            .load(promotionDetailUI.detailPicture)
            .into(binding.image)
    }

    private fun fillForYouProductSlider(categoryDetailUI: CategoryDetailUI) {
        bestForYouProductsSliderFragment.updateData(listOf(categoryDetailUI))
    }

    private fun fillPromotionProductRecycler(categoryDetailUI: CategoryDetailUI?) {
        when(categoryDetailUI) {
            null -> {
                binding.promotionProductsTitle.visibility = View.GONE
                binding.productRecycler.visibility = View.GONE
            }
            else -> {
                val diffUtil = ProductDiffUtilCallback(
                    oldList = linearProductAdapter.productUIList,
                    newList = categoryDetailUI.productUIList
                )

                DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                    linearProductAdapter.productUIList = categoryDetailUI.productUIList
                    diffResult.dispatchUpdatesTo(linearProductAdapter)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}*/
