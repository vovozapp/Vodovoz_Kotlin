package com.vodovoz.app.ui.fragment.product_details

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.vodovoz.app.R
import com.vodovoz.app.databinding.BsSelectionReplacementProductsBinding
import com.vodovoz.app.databinding.FragmentProductDetailsBinding
import com.vodovoz.app.ui.adapter.*
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.decoration.CommentMarginDecoration
import com.vodovoz.app.ui.decoration.PriceMarginDecoration
import com.vodovoz.app.ui.decoration.SearchMarginDecoration
import com.vodovoz.app.ui.diffUtils.DetailPictureDiffUtilCallback
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setCommentQuantity
import com.vodovoz.app.ui.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.fragment.product_info.ProductInfoFragment
import com.vodovoz.app.ui.fragment.product_properties.ProductPropertiesFragment
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderFragment
import com.vodovoz.app.ui.fragment.slider.promotion_slider.PromotionsSliderFragment
import com.vodovoz.app.ui.fragment.some_products_by_brand.SomeProductsByBrandFragment
import com.vodovoz.app.ui.fragment.some_products_maybe_like.SomeProductsMaybeLikeFragment
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setDiscountPercent
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setMinimalPriceText
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setPriceCondition
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setPricePerUnitText
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.extensions.ViewExtensions.onRenderFinished
import com.vodovoz.app.ui.fragment.profile.ProfileFragmentDirections
import com.vodovoz.app.ui.fragment.replacement_product.ReplacementProductsSelectionBS
import com.vodovoz.app.ui.model.*
import com.vodovoz.app.ui.model.custom.ProductDetailBundleUI
import com.vodovoz.app.ui.model.custom.PromotionsSliderBundleUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

@SuppressLint("NotifyDataSetChanged")
class ProductDetailsFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentProductDetailsBinding
    private lateinit var viewModel: ProductDetailsViewModel

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
        ProductsSliderFragment.newInstance(ProductsSliderConfig(containShowAllButton = false))
    }

    private val byWithProductsSliderFragment: ProductsSliderFragment by lazy {
        ProductsSliderFragment.newInstance(ProductsSliderConfig(containShowAllButton = false))
    }

    private val promotionsSliderFragment: PromotionsSliderFragment by lazy { PromotionsSliderFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initCallbacks()
        initViewModel()
        getArgs()
        subscribeSubjects()
    }

    private fun getArgs() {
        viewModel.updateArgs(ProductDetailsFragmentArgs.fromBundle(requireArguments()).productId)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[ProductDetailsViewModel::class.java]
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
            findNavController().navigate(ProductDetailsFragmentDirections.actionToSearchFragment().apply { this.query = query })
        }.addTo(compositeDisposable)
    }

    private fun initCallbacks() {
        byWithProductsSliderFragment.initCallbacks(
            iOnProductClick = { productId -> onProductClickSubject.onNext(productId) },
            iOnFavoriteClick = { pair -> onFavoriteClickSubject.onNext(pair) },
            iOnChangeProductQuantity = { pair -> onChangeProductQuantitySubject.onNext(pair) },
            iOnShowAllProductsClick = {}
        )
        recommendProductsSliderFragment.initCallbacks(
            iOnProductClick = { productId -> onProductClickSubject.onNext(productId) },
            iOnFavoriteClick = { pair -> onFavoriteClickSubject.onNext(pair) },
            iOnChangeProductQuantity = { pair -> onChangeProductQuantitySubject.onNext(pair) },
            iOnShowAllProductsClick = {}
        )
        promotionsSliderFragment.initCallbacks(
            iOnProductClick = {},
            iOnPromotionClick = { promotionId ->
                 findNavController().navigate(ProductDetailsFragmentDirections.actionToPromotionDetailFragment(promotionId))
            },
            iOnShowAllPromotionsClick = {},
            iOnFavoriteClick = { pair -> onFavoriteClickSubject.onNext(pair) },
            iOnChangeProductQuantity = { pair -> onChangeProductQuantitySubject.onNext(pair) }
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
        binding.rvSearchWords.addItemDecoration(SearchMarginDecoration(space))
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
                    binding.imgFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.png_ic_favorite))
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
                binding.tvCommentAmount.text = "Нет отзывов"
                binding.tvCommentAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_gray))
            }
            else -> {
                binding.tvCommentAmount.setCommentQuantity(productDetailBundle.productDetailUI.commentsAmount)
                binding.tvCommentAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.bluePrimary))
            }
        }

        //Favorite
        when(productDetailBundle.productDetailUI.isFavorite) {
            false -> binding.imgFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.png_ic_favorite))
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
        compositeDisposable.clear()
    }

}