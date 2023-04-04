package com.vodovoz.app.feature.productdetail

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.FragmentProductDetailsFlowBinding
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.PromotionsClickListener
import com.vodovoz.app.feature.productdetail.adapter.ProductDetailsClickListener
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.feature.replacement.ReplacementProductsSelectionBS
import com.vodovoz.app.ui.model.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailsFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_product_details_flow

    private val binding: FragmentProductDetailsFlowBinding by viewBinding { FragmentProductDetailsFlowBinding.bind(contentView) }

    private val viewModel: ProductDetailsFlowViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    val args: ProductDetailsFragmentArgs by navArgs()

    private val productDetailsController by lazy {
        ProductDetailsController(
            listener = getProductDetailsClickListener(),
            productsClickListener = getProductsClickListener(),
            productsShowAllListener = getProductsShowClickListener(),
            promotionsClickListener = getPromotionsClickListener(),
            cartManager = cartManager,
            likeManager = likeManager,
            requireContext()
        )
    }

    private val productDetailFabController by lazy {
        ProductDetailFabController(
            context = requireContext(),
            amountTv = binding.floatingAmountController.amount,
            circleAmountTv = binding.floatingAmountController.circleAmount,
            viewModel = viewModel,
            navController = findNavController(),
            addIv = binding.floatingAmountController.add,
            reduceIv = binding.floatingAmountController.reduceAmount,
            increaseIv = binding.floatingAmountController.increaseAmount,
            amountDeployed = binding.floatingAmountController.amountControllerDeployed
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.fetchProductDetail()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeState()
        observeResultLiveData()
        observeFabCartState()
        bindErrorRefresh { viewModel.fetchProductDetail() }

        productDetailsController.bind(binding.mainRv, binding.floatingAmountControllerContainer)
    }

    private fun observeFabCartState() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeUpdateFab()
                .collect{
                    productDetailFabController.updateFabQuantity(it)
                }
        }
    }

    private fun observeState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect { detailState ->

                    if (detailState.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }
                    productDetailsController.submitList(
                        listOfNotNull(
                            detailState.detailHeader,
                            detailState.detailPrices.takeIf { it?.priceUiList?.size != 1 },
                            detailState.detailServices.takeIf { it?.items?.size != 0 },
                            detailState.detailTabs,
                            detailState.detailCatAndBrand,
                            detailState.detailBrandList.takeIf { it.productUiList.isNotEmpty() },
                            detailState.detailRecommendsProducts.takeIf { it?.items?.first()?.productUIList?.size != 0 },
                            detailState.detailPromotions.takeIf { it?.items?.promotionUIList?.size != 0 },
                            detailState.detailMaybeLikeProducts.takeIf { it.productUiList.isNotEmpty() },
                            detailState.detailSearchWord.takeIf { it?.searchWordList?.size != 0 },
                            detailState.detailBuyWith.takeIf { it?.items?.first()?.productUIList?.size != 0 },
                            detailState.detailComments
                        )
                    )

                    productDetailFabController.bindFab(
                        header = detailState.detailHeader,
                        oldPriceTv = binding.tvFloatingOldPrice,
                        miniDetailIv = binding.miniDetailImage,
                        currentPriceTv = binding.tvFloatingCurrentPrice,
                        conditionTv = binding.tvFloatingPriceCondition
                    )

                    showError(detailState.error)
                }
        }
    }

    private fun getProductDetailsClickListener(): ProductDetailsClickListener {
        return object : ProductDetailsClickListener {
            override fun share(intent: Intent) {
                startActivity(intent)
            }

            override fun backPress() {
                findNavController().popBackStack()
            }

            override fun navigateToReplacement(
                detailPicture: String,
                products: Array<ProductUI>,
                id: Long,
                name: String,
                categoryId: Long?
            ) {
                findNavController().navigate(
                    ProductDetailsFragmentDirections.actionToReplacementProductsSelectionBS(
                        detailPicture, products, id, name, categoryId =  categoryId ?: -1L
                    )
                )
            }

            override fun onTvCommentAmount(productId: Long) {
                findNavController().navigate(ProductDetailsFragmentDirections.actionToAllCommentsByProductDialogFragment(productId))
            }

            override fun onYouTubeClick(videoCode: String) {
                findNavController().navigate(ProductDetailsFragmentDirections.actionToYouTubeVideoFragmentDialog(videoCode))
            }

            override fun onDetailPictureClick(currentItem: Int, detailPictureList: Array<String>) {
                findNavController().navigate(ProductDetailsFragmentDirections.actionToFullScreenDetailPicturesSliderFragment(
                    currentItem,
                    detailPictureList
                ))
            }

            override fun showProductsByCategory(id: Long) {
                findNavController().navigate(
                    ProductDetailsFragmentDirections.actionToPaginatedProductsCatalogFragment(id)
                )
            }

            override fun showProductsByBrand(id: Long) {
                findNavController().navigate(
                    ProductDetailsFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                        PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Brand(id)
                    )
                )
            }

            override fun onNextPageBrandProductsClick() {
                viewModel.nextPageBrandProducts()
            }

            override fun onNextPageMaybeLikeClick() {
                viewModel.nextPageMaybeLikeProducts()
            }

            override fun onQueryClick(query: String) {
                findNavController().navigate(ProductDetailsFragmentDirections.actionToSearchFragment(query))
            }

            override fun onSendComment(id: Long) {
                if (viewModel.isLoginAlready()) {
                    findNavController().navigate(ProductDetailsFragmentDirections.actionToSendCommentAboutProductFragment(id))
                } else {
                    findNavController().navigate(R.id.profileFragment)
                }
            }

            override fun onShowAllComments(id: Long) {
                findNavController().navigate(ProductDetailsFragmentDirections.actionToAllCommentsByProductDialogFragment(id))
            }
        }
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                findNavController().navigate(ProductDetailsFragmentDirections.actionToSelf(id))
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                when(viewModel.isLoginAlready()) {
                    true -> findNavController().navigate(ProductDetailsFragmentDirections.actionToPreOrderBS(id, name, detailPicture))
                    false -> findNavController().navigate(ProductDetailsFragmentDirections.actionToProfileFragment())
                }
            }

            override fun onChangeProductQuantity(id: Long, cartQuantity: Int, oldQuantity: Int) {
                viewModel.changeCart(id, cartQuantity, oldQuantity)
            }

            override fun onFavoriteClick(id: Long, isFavorite: Boolean) {
                viewModel.changeFavoriteStatus(id, isFavorite)
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

    private fun getPromotionsClickListener() : PromotionsClickListener {
        return object : PromotionsClickListener {
            override fun onPromotionClick(id: Long) {
                findNavController().navigate(ProductDetailsFragmentDirections.actionToPromotionDetailFragment(id))
            }
            override fun onShowAllPromotionsClick() {}
        }
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(ReplacementProductsSelectionBS.SELECTED_PRODUCT_ID)
            ?.observe(viewLifecycleOwner) { productId ->
                if (findNavController().currentDestination?.id == R.id.replacementProductsSelectionBS) {
                    findNavController().popBackStack()
                    findNavController().navigate(ProductDetailsFragmentDirections.actionToSelf(productId))
                }
            }
    }

    override fun onStop() {
        super.onStop()
        productDetailFabController.stopTimer()
    }
}

/*
@SuppressLint("NotifyDataSetChanged")
@AndroidEntryPoint
class ProductDetailsFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentProductDetailsBinding
    private val viewModel: ProductDetailsViewModel by viewModels()

    private val compositeDisposable = CompositeDisposable()

    private val onServiceClickSubject: PublishSubject<String> = PublishSubject.create()
    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onPromotionClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onQueryClickSubject: PublishSubject<String> = PublishSubject.create()
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>> = PublishSubject.create()
    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>> = PublishSubject.create()

    private val detailPicturePagerAdapter = DetailPicturePagerAdapter(
        iOnProductDetailPictureClick = {
            findNavController().navigate(ProductDetailsFragmentDirections.actionToFullScreenDetailPicturesSliderFragment(
                binding.vpPictures.currentItem,
                viewModel.productDetailBundleUI.productDetailUI.detailPictureList.toTypedArray()
            ))
        }
    )

    private val pricesAdapter = PricesAdapter()
    private val commentWithAvatarAdapter = CommentsWithAvatarAdapter()
    private val searchWordAdapter = SearchWordsAdapter(onQueryClickSubject = onQueryClickSubject)

    private var aboutProductFragment: ProductInfoFragment? = null
    private var propertiesFragment: ProductPropertiesFragment? = null

    private val space: Int by lazy { resources.getDimension(R.dimen.space_16).toInt() }


    private val recommendProductsSliderFragment: ProductsSliderFragment by lazy {
        ProductsSliderFragment.newInstance(ProductsSliderConfig(
            containShowAllButton = false,
            largeTitle = true
        ), true)
    }

    private val byWithProductsSliderFragment: ProductsSliderFragment by lazy {
        ProductsSliderFragment.newInstance(ProductsSliderConfig(
            containShowAllButton = false,
            largeTitle = true
        ))
    }

    private val promotionsSliderFragment: PromotionsSliderFragment by lazy { PromotionsSliderFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initCallbacks()
        getArgs()
        subscribeSubjects()
    }

    private fun getArgs() {
        viewModel.updateArgs(ProductDetailsFragmentArgs.fromBundle(requireArguments()).productId)
    }

    private fun subscribeSubjects() {
        onProductClickSubject.subscribeBy { productId ->
            findNavController().navigate(ProductDetailsFragmentDirections.actionToSelf(productId))
        }.addTo(compositeDisposable)
        onFavoriteClickSubject.subscribeBy { pair ->
            viewModel.changeFavoriteStatus(pair.first, pair.second)
        }.addTo(compositeDisposable)
        onChangeProductQuantitySubject.subscribeBy { pair ->
            viewModel.changeCart(pair.first, pair.second)
        }.addTo(compositeDisposable)
        onPromotionClickSubject.subscribeBy { promotionId ->
            findNavController().navigate(ProductDetailsFragmentDirections.actionToPromotionDetailFragment(promotionId))
        }.addTo(compositeDisposable)
        onQueryClickSubject.subscribeBy { query ->
            findNavController().navigate(ProductDetailsFragmentDirections.actionToSearchFragment(query))
        }.addTo(compositeDisposable)
    }

    private fun initCallbacks() {
        byWithProductsSliderFragment.initCallbacks(
            iOnProductClick = { productId -> onProductClickSubject.onNext(productId) },
            iOnFavoriteClick = { pair -> onFavoriteClickSubject.onNext(pair) },
            iOnChangeProductQuantity = { pair -> onChangeProductQuantitySubject.onNext(pair) },
            iOnShowAllProductsClick = {},
            onNotAvailableMore = {},
            onNotifyWhenBeAvailable = { id, name, picture ->
                when(viewModel.isAlreadyLogin()) {
                    true -> findNavController().navigate(ProductDetailsFragmentDirections.actionToPreOrderBS(id, name, picture))
                    false -> findNavController().navigate(ProductDetailsFragmentDirections.actionToProfileFragment())
                }
            }
        )
        recommendProductsSliderFragment.initCallbacks(
            iOnProductClick = { productId -> onProductClickSubject.onNext(productId) },
            iOnFavoriteClick = { pair -> onFavoriteClickSubject.onNext(pair) },
            iOnChangeProductQuantity = { pair -> onChangeProductQuantitySubject.onNext(pair) },
            iOnShowAllProductsClick = {},
            onNotAvailableMore = {},
            onNotifyWhenBeAvailable = { id, name, picture ->
                when(viewModel.isAlreadyLogin()) {
                    true -> findNavController().navigate(ProductDetailsFragmentDirections.actionToPreOrderBS(id, name, picture))
                    false -> findNavController().navigate(ProductDetailsFragmentDirections.actionToProfileFragment())
                }
            }
        )
        promotionsSliderFragment.initCallbacks(
            iOnProductClick = {},
            iOnPromotionClick = { promotionId ->
                 findNavController().navigate(ProductDetailsFragmentDirections.actionToPromotionDetailFragment(promotionId))
            },
            iOnShowAllPromotionsClick = {},
            iOnFavoriteClick = { pair -> onFavoriteClickSubject.onNext(pair) },
            iOnChangeProductQuantity = { pair -> onChangeProductQuantitySubject.onNext(pair) },
            onNotAvailableMore = {},
            onNotifyWhenBeAvailable = { id, name, picture ->
                when(viewModel.isAlreadyLogin()) {
                    true -> findNavController().navigate(ProductDetailsFragmentDirections.actionToPreOrderBS(id, name, picture))
                    false -> findNavController().navigate(ProductDetailsFragmentDirections.actionToProfileFragment())
                }
            }
        )
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup,
    ) = FragmentProductDetailsBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initDetailPictureRecycler()
        initPriceRecycler()
        initHeader()
        initAboutProductPager()
        initCommentRecycler()
        initSearchWordRecycler()
        initButtons()
        initAmountController()
        initProductsSliders()
        observeViewModel()
        observeResultLiveData()
    }

    override fun update() {
        viewModel.updateProductDetail()
    }

    private fun initDetailPictureRecycler() {
        binding.imgBack.setOnClickListener { findNavController().popBackStack() }
        binding.vpPictures.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpPictures.adapter = detailPicturePagerAdapter
        TabLayoutMediator(binding.tlIndicators, binding.vpPictures) { _, _ -> }.attach()
    }

    private fun initPriceRecycler() {
        binding.rvPrices.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvPrices.addItemDecoration(PriceMarginDecoration(space))
        binding.rvPrices.adapter = pricesAdapter
    }

    private fun initCommentRecycler() {
        binding.commentRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.commentRecycler.adapter = commentWithAvatarAdapter
        binding.commentRecycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.commentRecycler.addItemDecoration(CommentMarginDecoration(space))
    }

    private fun initProductsSliders() {
        childFragmentManager.beginTransaction().replace(
            R.id.byWithProductSliderFragment,
            byWithProductsSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.recommendProductSliderFragment,
            recommendProductsSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.promotionsSliderFragment,
            promotionsSliderFragment
        ).commit()
    }

    private fun initSearchWordRecycler() {
        binding.rvSearchWords.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvSearchWords.adapter = searchWordAdapter
        binding.rvSearchWords.addItemDecoration(SearchMarginDecoration(space/2))
    }

    private val amountControllerTimer = object: CountDownTimer(3000, 3000) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            viewModel.changeCart(
                viewModel.productDetailBundleUI.productDetailUI.id,
                viewModel.productDetailBundleUI.productDetailUI.cartQuantity
            )
            hideAmountController()
        }
    }

    private fun initAmountController() {
        binding.amountController.add.setOnClickListener { add() }
        binding.floatingAmountController.add.setOnClickListener { add() }
        binding.amountController.reduceAmount.setOnClickListener { reduceAmount() }
        binding.floatingAmountController.reduceAmount.setOnClickListener { reduceAmount() }
        binding.amountController.increaseAmount.setOnClickListener { increaseAmount() }
        binding.floatingAmountController.increaseAmount.setOnClickListener { increaseAmount() }

        binding.clHeader.onRenderFinished { _, height ->
            binding.nsvContent.setOnScrollChangeListener(
                NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                    if (scrollY > height) {
                        binding.floatingAmountControllerContainer.visibility = View.VISIBLE
                    } else {
                        binding.floatingAmountControllerContainer.visibility = View.INVISIBLE
                    }
                }
            )
        }
    }

    private fun reduceAmount() {
        with(viewModel.productDetailBundleUI) {
            productDetailUI.cartQuantity--
            if (productDetailUI.cartQuantity < 0) productDetailUI.cartQuantity = 0
            amountControllerTimer.cancel()
            amountControllerTimer.start()
            updateCartQuantity()
        }
    }

    private fun increaseAmount() {
        viewModel.productDetailBundleUI.productDetailUI.cartQuantity++
        amountControllerTimer.cancel()
        amountControllerTimer.start()
        updateCartQuantity()
    }

    private fun add() {
        if (viewModel.productDetailBundleUI.productDetailUI.leftItems == 0) {
            viewModel.productDetailBundleUI.replacementProductsCategoryDetail?.let {
                if (it.productUIList.isNotEmpty()) {
                    findNavController().navigate(ProductDetailsFragmentDirections.actionToReplacementProductsSelectionBS(
                        viewModel.productDetailBundleUI.productDetailUI.detailPictureList.first(),
                        it.productUIList.toTypedArray(),
                        viewModel.productId!!,
                        viewModel.productDetailBundleUI.productDetailUI.name
                    ))
                }
            }
        } else {
            if (viewModel.productDetailBundleUI.productDetailUI.cartQuantity == 0) {
                viewModel.productDetailBundleUI.productDetailUI.cartQuantity++
                updateCartQuantity()
            }
            showAmountController()
        }
    }


    private fun updateCartQuantity() {
        with(viewModel.productDetailBundleUI) {
            if (productDetailUI.cartQuantity < 0) {
                productDetailUI.cartQuantity = 0
            }
            binding.amountController.amount.text = productDetailUI.cartQuantity.toString()
            binding.amountController.circleAmount.text = productDetailUI.cartQuantity.toString()
            binding.floatingAmountController.amount.text = productDetailUI.cartQuantity.toString()
            binding.floatingAmountController.circleAmount.text = productDetailUI.cartQuantity.toString()
        }
    }

    private fun showAmountController() {
        binding.amountController.circleAmount.visibility = View.GONE
        binding.amountController.add.visibility = View.GONE
        binding.amountController.amountControllerDeployed.visibility = View.VISIBLE
        binding.floatingAmountController.circleAmount.visibility = View.GONE
        binding.floatingAmountController.add.visibility = View.GONE
        binding.floatingAmountController.amountControllerDeployed.visibility = View.VISIBLE
        amountControllerTimer.start()
    }

    private fun hideAmountController() {
        if (viewModel.productDetailBundleUI.productDetailUI.cartQuantity > 0) {
            binding.amountController.circleAmount.visibility = View.VISIBLE
            binding.floatingAmountController.circleAmount.visibility = View.VISIBLE
        }
        binding.amountController.add.visibility = View.VISIBLE
        binding.amountController.amountControllerDeployed.visibility = View.GONE
        binding.floatingAmountController.add.visibility = View.VISIBLE
        binding.floatingAmountController.amountControllerDeployed.visibility = View.GONE
    }

    private fun initAboutProductPager() {
        binding.aboutTabs.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val fragment = when(tab?.position) {
                        0 -> aboutProductFragment
                        1 -> propertiesFragment
                        else -> null
                    }
                    fragment?.let {
                        childFragmentManager.beginTransaction()
                            .replace(R.id.aboutProductContainer, fragment)
                            .commit()
                    }
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            }
        )
    }

    private fun initHeader() {
        binding.tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.tvFloatingOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.imgShare.setOnClickListener {
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, viewModel.productDetailBundleUI.productDetailUI.shareUrl)
                },
                "Shearing Option"
            ).let { startActivity(it) }
        }
        binding.tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.tvFloatingOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

        binding.imgFavorite.setOnClickListener {
            when(viewModel.productDetailBundleUI.productDetailUI.isFavorite) {
                true -> {
                    viewModel.productDetailBundleUI.productDetailUI.isFavorite = false
                    binding.imgFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_black))
                }
                false -> {
                    viewModel.productDetailBundleUI.productDetailUI.isFavorite = true
                    binding.imgFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.png_ic_favorite_red))
                }
            }
            onFavoriteClickSubject.onNext(Pair(
                viewModel.productDetailBundleUI.productDetailUI.id,
                viewModel.productDetailBundleUI.productDetailUI.isFavorite
            ))
        }
    }

    private fun initButtons() {
        binding.tvBrand.setOnClickListener { showProductsByBrand() }
        binding.clBrandContainer.setOnClickListener { showProductsByBrand() }
        binding.clCategoryContainer.setOnClickListener { showProductsByCategory() }
        binding.tvCommentAmount.setOnClickListener {
            if (viewModel.productDetailBundleUI.productDetailUI.commentsAmount != 0) {
                findNavController().navigate(ProductDetailsFragmentDirections.actionToAllCommentsByProductDialogFragment(
                    viewModel.productId!!
                ))
            }
        }
        binding.tvSendComment.setOnClickListener {
            if (viewModel.isLogin()) {
                findNavController().navigate(ProductDetailsFragmentDirections.actionToSendCommentAboutProductFragment(
                    viewModel.productId!!
                ))
            } else {
                findNavController().navigate(R.id.profileFragment)
            }
        }
        binding.cwPlayVideo.setOnClickListener {
            viewModel.productDetailBundleUI.productDetailUI.youtubeVideoCode?.let { videoCode ->
                findNavController().navigate(ProductDetailsFragmentDirections.actionToYouTubeVideoFragmentDialog(videoCode))
            }
        }
        binding.tvShowAllComment.setOnClickListener {
            findNavController().navigate(ProductDetailsFragmentDirections.actionToAllCommentsByProductDialogFragment(
                viewModel.productId!!
            ))
        }
    }

    private fun showProductsByCategory() {
        findNavController().navigate(
            ProductDetailsFragmentDirections.actionToPaginatedProductsCatalogFragment(
                viewModel.productDetailBundleUI.categoryUI.id!!
            )
        )
    }

    private fun showProductsByBrand() {
        viewModel.productDetailBundleUI.productDetailUI.brandUI?.id?.let { brandId ->
            findNavController().navigate(
                ProductDetailsFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Brand(brandId)
                )
            )
        }
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(ReplacementProductsSelectionBS.SELECTED_PRODUCT_ID)
            ?.observe(viewLifecycleOwner) { productId ->
                if (findNavController().currentDestination?.id == R.id.replacementProductsSelectionBS) {
                    findNavController().popBackStack()
                    findNavController().navigate(ProductDetailsFragmentDirections.actionToSelf(productId))
                }
            }
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Hide -> onStateHide()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Loading -> onStateLoading()
                is ViewState.Success -> onStateSuccess()
            }
        }

        viewModel.productDetailBundleUILD.observe(viewLifecycleOwner) { productDetailBundleUI ->
            Toast.makeText(requireContext(), viewModel.productDetailBundleUI.productDetailUI.youtubeVideoCode.toString(), Toast.LENGTH_SHORT).show()

            fillView(productDetailBundleUI)
        }
    }

    private fun fillView(productDetailBundleUI: ProductDetailBundleUI) {
        fillHeader(productDetailBundleUI)
        fillDetailPictureRecycler(productDetailBundleUI.productDetailUI.detailPictureList)
        binding.tlIndicators.isVisible = productDetailBundleUI.productDetailUI.detailPictureList.size > 1
        fillAboutProduct(productDetailBundleUI.productDetailUI)
        fillCategoryAndBrandSection(productDetailBundleUI)
        fillCommentRecycler(productDetailBundleUI.commentUIList)
        fillPromotionSlider(productDetailBundleUI.promotionUIList)
        fillSearchWordRecycler(productDetailBundleUI.searchWordList)
        fillPaginatedMaybeLikeProductList()
        fillServicesRecycler(productDetailBundleUI.serviceUIList)
        fillBrandProductList(
            productId = productDetailBundleUI.productDetailUI.id,
            brandId = productDetailBundleUI.productDetailUI.brandUI?.id
        )
        fillByWithProductSlider(CategoryDetailUI(
            name = "С этим товаром покупают",
            productAmount = productDetailBundleUI.buyWithProductUIList.size,
            productUIList = productDetailBundleUI.buyWithProductUIList
        ))
        fillRecommendProductSliderFragment(CategoryDetailUI(
            name = "Рекомендуем также",
            productAmount = productDetailBundleUI.recommendProductUIList.size,
            productUIList = productDetailBundleUI.recommendProductUIList
        ))
    }

    private fun fillBrandProductList(
        productId: Long,
        brandId: Long?,
    ) {
        brandId?.let {
            childFragmentManager.beginTransaction()
                .replace(R.id.brandProductListFragment, SomeProductsByBrandFragment.newInstance(
                    productId = productId,
                    brandId = brandId,
                    onProductClickSubject = onProductClickSubject
                )).commit()
        }
    }

    private fun fillServicesRecycler(serviceUIList: List<ServiceUI>) {
        when(serviceUIList.isEmpty()) {
            true -> binding.servicesContainer.visibility = View.GONE
            false -> binding.servicesContainer.visibility = View.VISIBLE
        }

        binding.rvServices.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvServices.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State,
                ) {
                    with(outRect) {
                        if (parent.getChildAdapterPosition(view) % 2 == 0) {
                            left = space
                            right = space/4
                        } else {
                            left = space/4
                            right = space
                        }
                        top = space
                        bottom = space
                    }
                }
            }
        )

        binding.rvServices.adapter = ServicesAdapter(
            serviceUIList = serviceUIList,
            onServiceClickSubject = onServiceClickSubject
        )
    }

    private fun fillSearchWordRecycler(searchWordList: List<String>) {
        searchWordAdapter.searchWordList = searchWordList
        searchWordAdapter.notifyDataSetChanged()

        when(searchWordList.isEmpty()) {
            true -> binding.searchWordContainer.visibility = View.GONE
            false -> binding.searchWordContainer.visibility = View.VISIBLE
        }
    }

    private fun fillAboutProduct(productDetailUI: ProductDetailUI) {
        binding.tvConsumerInfo.text = productDetailUI.consumerInfo
        aboutProductFragment = ProductInfoFragment.newInstance(productDetailUI.detailText)
        propertiesFragment = ProductPropertiesFragment.newInstance(productDetailUI.propertiesGroupUIList)
        childFragmentManager.beginTransaction()
            .replace(R.id.aboutProductContainer, aboutProductFragment!!)
            .commit()
    }
    private fun fillPaginatedMaybeLikeProductList() {
        childFragmentManager.beginTransaction()
            .replace(R.id.paginatedMaybeLikeProductListFragment, SomeProductsMaybeLikeFragment.newInstance(
                onProductClickSubject = onProductClickSubject
            ))
            .commit()
    }

    @SuppressLint("Range")
    private fun fillHeader(productDetailBundle: ProductDetailBundleUI) {
        productDetailBundle.productDetailUI.brandUI?.let { binding.tvBrand.text = it.name }

        binding.tvName.text = productDetailBundle.productDetailUI.name
        binding.rbRating.rating = productDetailBundle.productDetailUI.rating.toFloat()

        Glide.with(requireContext())
            .load(productDetailBundle.productDetailUI.detailPictureList.first())
            .into(binding.miniDetailImage)


        when(productDetailBundle.productDetailUI.youtubeVideoCode.isEmpty()) {
            true -> binding.cwPlayVideo.visibility = View.GONE
            false -> binding.cwPlayVideo.visibility = View.VISIBLE
        }

        //If left items = 0
        when(productDetailBundle.productDetailUI.leftItems == 0) {
            true -> {
                when(productDetailBundle.replacementProductsCategoryDetail?.productUIList?.isEmpty()) {
                    true -> {
                        binding.amountController.add.setBackgroundResource(R.drawable.bkg_button_gray_circle_normal)
                        binding.amountController.add.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.png_alert))
                        binding.floatingAmountController.add.setBackgroundResource(R.drawable.bkg_button_gray_circle_normal)
                        binding.floatingAmountController.add.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.png_alert))
                    }
                    false -> {
                        binding.amountController.add.setBackgroundResource(R.drawable.bkg_button_orange_circle_normal)
                        binding.amountController.add.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_swap))
                        binding.floatingAmountController.add.setBackgroundResource(R.drawable.bkg_button_orange_circle_normal)
                        binding.floatingAmountController.add.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_swap))
                    }
                }
            }
            false -> {
                binding.amountController.add.setBackgroundResource(R.drawable.bkg_button_green_circle_normal)
                binding.amountController.add.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.png_cart))
                binding.floatingAmountController.add.setBackgroundResource(R.drawable.bkg_button_green_circle_normal)
                binding.floatingAmountController.add.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.png_cart))
            }
        }

        //Price per unit / or order quantity
        when(productDetailBundle.productDetailUI.pricePerUnit != 0) {
            true -> {
                binding.tvPricePerUnit.visibility = View.VISIBLE
                binding.tvPricePerUnit.setPricePerUnitText(productDetailBundle.productDetailUI.pricePerUnit)
            }
            false -> binding.tvPricePerUnit.visibility = View.GONE
        }

        //Price
        var haveDiscount = false
        when(productDetailBundle.productDetailUI.priceUIList.size) {
            1 -> {
                binding.tvCurrentPrice.setPriceText(productDetailBundle.productDetailUI.priceUIList.first().currentPrice)
                binding.tvFloatingCurrentPrice.setPriceText(productDetailBundle.productDetailUI.priceUIList.first().currentPrice)
                binding.tvOldPrice.setPriceText(productDetailBundle.productDetailUI.priceUIList.first().oldPrice)
                binding.tvFloatingOldPrice.setPriceText(productDetailBundle.productDetailUI.priceUIList.first().oldPrice)
                binding.tvPriceCondition.visibility = View.GONE
                binding.tvFloatingPriceCondition.visibility = View.GONE
                if (productDetailBundle.productDetailUI.priceUIList.first().currentPrice <
                    productDetailBundle.productDetailUI.priceUIList.first().oldPrice) haveDiscount = true
            }
            else -> {
                val minimalPrice = productDetailBundle.productDetailUI.priceUIList.maxByOrNull { it.requiredAmount }!!
                binding.tvCurrentPrice.setMinimalPriceText(minimalPrice.currentPrice)
                binding.tvFloatingCurrentPrice.setMinimalPriceText(minimalPrice.currentPrice)
                binding.tvPriceCondition.setPriceCondition(minimalPrice.requiredAmount)
                binding.tvFloatingPriceCondition.setPriceCondition(minimalPrice.requiredAmount)
                binding.tvPriceCondition.visibility = View.VISIBLE
                binding.tvFloatingPriceCondition.visibility = View.VISIBLE
                binding.tvPricePerUnit.visibility = View.GONE
                pricesAdapter.priceUIList = productDetailBundle.productDetailUI.priceUIList
                pricesAdapter.notifyDataSetChanged()
            }
        }
        when(haveDiscount) {
            true -> {
                binding.tvCurrentPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                binding.tvFloatingCurrentPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                binding.tvOldPrice.visibility = View.VISIBLE
                binding.tvFloatingOldPrice.visibility = View.VISIBLE
            }
            false -> {
                binding.tvCurrentPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
                binding.tvFloatingCurrentPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_black))
                binding.tvOldPrice.visibility = View.GONE
                binding.tvFloatingOldPrice.visibility = View.GONE
            }
        }

        //Cart amount
        binding.amountController.circleAmount.text = productDetailBundle.productDetailUI.cartQuantity.toString()
        binding.amountController.amount.text = productDetailBundle.productDetailUI.cartQuantity.toString()
        binding.floatingAmountController.circleAmount.text = productDetailBundle.productDetailUI.cartQuantity.toString()
        binding.floatingAmountController.amount.text = productDetailBundle.productDetailUI.cartQuantity.toString()


        when (productDetailBundle.productDetailUI.cartQuantity > 0) {
            true -> {
                binding.amountController.circleAmount.visibility = View.VISIBLE
                binding.floatingAmountController.circleAmount.visibility = View.VISIBLE
            }
            false -> {
                binding.amountController.circleAmount.visibility = View.GONE
                binding.floatingAmountController.circleAmount.visibility = View.GONE
            }
        }

        //Comment
        when (productDetailBundle.productDetailUI.commentsAmount == 0) {
            true -> {
                binding.tvCommentAmount.text = ""
                binding.tvCommentAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_gray))
            }
            else -> {
                binding.tvCommentAmount.setCommentQuantity(productDetailBundle.productDetailUI.commentsAmount)
                binding.tvCommentAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.bluePrimary))
            }
        }

        //Favorite
        when(productDetailBundle.productDetailUI.isFavorite) {
            false -> binding.imgFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_black))
            true -> binding.imgFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.png_ic_favorite_red))
        }

        //Status
        var isNotHaveStatuses = true
        when (productDetailBundle.productDetailUI.status.isEmpty()) {
            true -> {}
            false -> {
                isNotHaveStatuses = false
                binding.cwStatusContainer.visibility = View.VISIBLE
                binding.tvStatus.text = productDetailBundle.productDetailUI.status
                binding.cwStatusContainer.setCardBackgroundColor(Color.parseColor(productDetailBundle.productDetailUI.statusColor))
            }
        }

        //DiscountPercent
        when(productDetailBundle.productDetailUI.priceUIList.size == 1 &&
                productDetailBundle.productDetailUI.priceUIList.first().currentPrice <
                productDetailBundle.productDetailUI.priceUIList.first().oldPrice) {
            true -> {
                binding.cwDiscountContainer.visibility = View.VISIBLE
                binding.tvDiscountPercent.setDiscountPercent(
                    newPrice = productDetailBundle.productDetailUI.priceUIList.first().currentPrice,
                    oldPrice = productDetailBundle.productDetailUI.priceUIList.first().oldPrice
                )
            }
            false -> binding.cwDiscountContainer.visibility = View.GONE
        }

        when(isNotHaveStatuses) {
            true -> binding.cgStatuses.visibility = View.GONE
            false -> binding.cgStatuses.visibility = View.VISIBLE
        }

        val diffUtil = DetailPictureDiffUtilCallback(
            oldList = detailPicturePagerAdapter.detailPictureUrlList,
            newList = productDetailBundle.productDetailUI.detailPictureList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            detailPicturePagerAdapter.detailPictureUrlList = productDetailBundle.productDetailUI.detailPictureList
            diffResult.dispatchUpdatesTo(detailPicturePagerAdapter)
        }
    }

    private fun fillCategoryAndBrandSection(productDetailBundleUI: ProductDetailBundleUI) {
        binding.tvCategoryName.text = productDetailBundleUI.categoryUI.name
        Glide.with(requireContext())
            .load(productDetailBundleUI.categoryUI.detailPicture)
            .into(binding.imgCategory)

        when(productDetailBundleUI.productDetailUI.brandUI) {
            null -> {
                binding.clBrandContainer.visibility = View.GONE
                binding.mdBetweenCategoryAndBrand.visibility = View.GONE
            }
            else -> {
                binding.clBrandContainer.visibility = View.VISIBLE
                binding.mdBetweenCategoryAndBrand.visibility = View.VISIBLE
                binding.tvBrandName.text = productDetailBundleUI.productDetailUI.brandUI.name
                Glide.with(requireContext())
                    .load(productDetailBundleUI.productDetailUI.brandUI.detailPicture)
                    .into(binding.imgBrand)
            }
        }
    }

    private fun fillPromotionSlider(promotionUIList: List<PromotionUI>) {
        when(promotionUIList.isEmpty()) {
            true -> binding.promotionsSliderFragment.visibility = View.GONE
            false -> binding.promotionsSliderFragment.visibility = View.VISIBLE
        }

        promotionsSliderFragment.updateData(PromotionsSliderBundleUI(
            "Товар учавствующий в акции",
            containShowAllButton = false,
            promotionUIList = promotionUIList
        ))
    }

    private fun fillByWithProductSlider(categoryDetailUI: CategoryDetailUI) {
        when(categoryDetailUI.productUIList.isEmpty()) {
            true -> binding.byWithProductSliderFragment.visibility = View.GONE
            false -> binding.byWithProductSliderFragment.visibility = View.VISIBLE
        }

        byWithProductsSliderFragment.updateData(listOf(categoryDetailUI))
    }

    private fun fillRecommendProductSliderFragment(categoryDetailUI: CategoryDetailUI) {
        when(categoryDetailUI.productUIList.isEmpty()) {
            true -> binding.recommendProductSliderFragment.visibility = View.GONE
            false -> binding.recommendProductSliderFragment.visibility = View.VISIBLE
        }

        recommendProductsSliderFragment.updateData(listOf(categoryDetailUI))
    }


    private fun fillCommentRecycler(commentUIList: List<CommentUI>) {
        binding.tvCommentAmountTitle.text = StringBuilder()
            .append("Отзывы (")
            .append(commentUIList.size)
            .append(")")
            .toString()
        commentWithAvatarAdapter.commentUiList = commentUIList
        commentWithAvatarAdapter.notifyDataSetChanged()

        if (commentUIList.isEmpty()) {
            binding.llCommentsTitleContainer.visibility = View.GONE
            binding.noCommentsTitle.visibility = View.VISIBLE
        }
    }

    private fun fillDetailPictureRecycler(detailPictureList: List<String>) {
        val diffUtil = DetailPictureDiffUtilCallback(
            oldList = detailPicturePagerAdapter.detailPictureUrlList,
            newList = detailPictureList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            detailPicturePagerAdapter.detailPictureUrlList = detailPictureList
            diffResult.dispatchUpdatesTo(detailPicturePagerAdapter)
        }
    }

    override fun onStop() {
        super.onStop()
        amountControllerTimer.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}*/
