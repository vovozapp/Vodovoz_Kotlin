package com.vodovoz.app.ui.components.fragment.product_detail

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentProductDetailBinding
import com.vodovoz.app.ui.components.adapter.CommentsWithAvatarAdapter
import com.vodovoz.app.ui.components.adapter.PricesAdapter
import com.vodovoz.app.ui.components.adapter.SearchWordsAdapter
import com.vodovoz.app.ui.components.adapter.ServicesAdapter
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.ViewStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.base.picturePagerAdapter.DetailPictureDiffUtilCallback
import com.vodovoz.app.ui.components.base.picturePagerAdapter.DetailPictureSliderAdapter
import com.vodovoz.app.ui.components.decoration.CommentMarginDecoration
import com.vodovoz.app.ui.components.decoration.PriceMarginDecoration
import com.vodovoz.app.ui.components.decoration.SearchMarginDecoration
import com.vodovoz.app.ui.components.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.components.fragment.product_info.ProductInfoFragment
import com.vodovoz.app.ui.components.fragment.product_properties.ProductPropertiesFragment
import com.vodovoz.app.ui.components.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.components.fragment.slider.products_slider.ProductsSliderFragment
import com.vodovoz.app.ui.components.fragment.slider.promotion_slider.PromotionsSliderFragment
import com.vodovoz.app.ui.components.fragment.some_products_by_brand.SomeProductsByBrandFragment
import com.vodovoz.app.ui.components.fragment.some_products_maybe_like.SomeProductsMaybeLikeFragment
import com.vodovoz.app.ui.components.interfaces.IOnFavoriteClick
import com.vodovoz.app.ui.components.interfaces.IOnProductClick
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setDiscountText
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.extensions.ViewExtensions.onRenderFinished
import com.vodovoz.app.ui.model.*
import com.vodovoz.app.ui.model.custom.ProductDetailBundleUI
import com.vodovoz.app.ui.model.custom.PromotionsSliderBundleUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

@SuppressLint("NotifyDataSetChanged")
class ProductDetailFragment : ViewStateBaseFragment(), IOnProductClick, IOnFavoriteClick {

    private lateinit var binding: FragmentProductDetailBinding
    private lateinit var viewModel: ProductDetailViewModel

    private val compositeDisposable = CompositeDisposable()

    private val onServiceClickSubject: PublishSubject<String> = PublishSubject.create()
    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onPromotionClickSubject: PublishSubject<Long> = PublishSubject.create()

    private val detailPictureSliderAdapter = DetailPictureSliderAdapter(
        iOnProductDetailPictureClick = {
            findNavController().navigate(ProductDetailFragmentDirections.actionToFullScreenDetailPicturesSliderFragment(
                binding.detailPicturePager.currentItem,
                viewModel.productDetailBundleUI.productDetailUI.detailPictureList.toTypedArray()
            ))
        }
    )

    private val pricesAdapter = PricesAdapter()
    private val commentWithAvatarAdapter = CommentsWithAvatarAdapter()
    private val searchWordAvatarAdapter = SearchWordsAdapter()

    private var aboutProductFragment: ProductInfoFragment? = null
    private var propertiesFragment: ProductPropertiesFragment? = null

    private val space: Int by lazy { resources.getDimension(R.dimen.primary_space).toInt() }


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
    }

    private fun getArgs() {
        viewModel.updateArgs(ProductDetailFragmentArgs.fromBundle(requireArguments()).productId)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[ProductDetailViewModel::class.java]
    }

    private fun initCallbacks() {
        byWithProductsSliderFragment.initCallbacks(
            iOnProductClick = this,
            iOnFavoriteClick = this,
            iOnChangeProductQuantity = { _, _ -> },
            iOnShowAllProductsClick = {}
        )
        recommendProductsSliderFragment.initCallbacks(
            iOnProductClick = this,
            iOnFavoriteClick = this,
            iOnChangeProductQuantity = { _, _ -> },
            iOnShowAllProductsClick = {}
        )
        promotionsSliderFragment.initCallbacks(
            iOnProductClick = {},
            iOnPromotionClick = { promotionId ->
                 findNavController().navigate(ProductDetailFragmentDirections.actionToPromotionDetailFragment(promotionId))
            },
            iOnShowAllPromotionsClick = {}
        )
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentProductDetailBinding.inflate(
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
    }

    override fun update() {
        viewModel.updateProductDetail()
    }

    private fun initDetailPictureRecycler() {
        binding.back.setOnClickListener { findNavController().popBackStack() }
        binding.detailPicturePager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.detailPicturePager.adapter = detailPictureSliderAdapter
        TabLayoutMediator(binding.tabIndicator, binding.detailPicturePager) { _, _ -> }.attach()
    }

    private fun initPriceRecycler() {
        binding.priceRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.priceRecycler.addItemDecoration(PriceMarginDecoration(space))
        binding.priceRecycler.adapter = pricesAdapter
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
        binding.searchWordRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.searchWordRecycler.adapter = searchWordAvatarAdapter
        binding.searchWordRecycler.addItemDecoration(SearchMarginDecoration(space))
    }

    private val amountControllerTimer = object: CountDownTimer(3000, 3000) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            viewModel.changeCart()
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

        binding.productHeaderContainer.onRenderFinished { _, height ->
            binding.contentScrollView.setOnScrollChangeListener(
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
        if (viewModel.productDetailBundleUI.productDetailUI.cartQuantity == 0) {
            viewModel.productDetailBundleUI.productDetailUI.cartQuantity++
            updateCartQuantity()
        }
        showAmountController()
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
        binding.oldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.floatingOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
    }

    private fun initButtons() {
        binding.brand.setOnClickListener { showProductsByBrand() }
        binding.brandContainer.setOnClickListener { showProductsByBrand() }
        binding.categoryContainer.setOnClickListener { showProductsByCategory() }
        binding.commentAmount.setOnClickListener {
            findNavController().navigate(ProductDetailFragmentDirections.actionToAllCommentsByProductDialogFragment(
                viewModel.productId!!
            ))
        }
        binding.sendComment.setOnClickListener {
            if (viewModel.isLogin()) {
                findNavController().navigate(ProductDetailFragmentDirections.actionToSendCommentAboutProductFragment(
                    viewModel.productId!!
                ))
            } else {
                findNavController().navigate(R.id.profileFragment)
            }
        }
        binding.playVideo.setOnClickListener {
            viewModel.productDetailBundleUI.productDetailUI.youtubeVideoCode?.let { videoCode ->
                findNavController().navigate(ProductDetailFragmentDirections.actionToYouTubeVideoFragmentDialog(videoCode))
            }
        }
        binding.showAllComment.setOnClickListener {
            findNavController().navigate(ProductDetailFragmentDirections.actionToAllCommentsByProductDialogFragment(
                viewModel.productId!!
            ))
        }
    }

    private fun showProductsByCategory() {
        findNavController().navigate(
            ProductDetailFragmentDirections.actionToPaginatedProductsCatalogFragment(
                viewModel.productDetailBundleUI.categoryUI.id!!
            )
        )
    }

    private fun showProductsByBrand() {
        viewModel.productDetailBundleUI.productDetailUI.brandUI?.id?.let { brandId ->
            findNavController().navigate(
                ProductDetailFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Brand(brandId)
                )
            )
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
        fillPrice(productDetailBundleUI.productDetailUI)
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
        brandId: Long?
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

        binding.servicesRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.servicesRecycler.addItemDecoration(
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

        binding.servicesRecycler.adapter = ServicesAdapter(
            serviceUIList = serviceUIList,
            onServiceClickSubject = onServiceClickSubject
        )
    }

    private fun fillSearchWordRecycler(searchWordList: List<String>) {
        searchWordAvatarAdapter.searchWordList = searchWordList
        searchWordAvatarAdapter.notifyDataSetChanged()

        when(searchWordList.isEmpty()) {
            true -> binding.searchWordContainer.visibility = View.GONE
            false -> binding.searchWordContainer.visibility = View.VISIBLE
        }
    }

    private fun fillAboutProduct(productDetailUI: ProductDetailUI) {
        binding.consumerInfo.text = productDetailUI.consumerInfo
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
        binding.name.text = productDetailBundle.productDetailUI.name
        binding.rating.rating = requireNotNull(productDetailBundle.productDetailUI.rating).toFloat()
        productDetailBundle.productDetailUI.brandUI?.let { binding.brand.text = it.name }

        when(productDetailBundle.productDetailUI.youtubeVideoCode) {
            null -> binding.playVideo.visibility = View.GONE
            else -> binding.playVideo.visibility = View.VISIBLE
        }

        Glide.with(requireContext())
            .load(productDetailBundle.productDetailUI.detailPictureList.first())
            .into(binding.miniDetailImage)

        if (productDetailBundle.productDetailUI.pricePerUnit != null) {
            binding.pricePerUnit.visibility = View.VISIBLE
            binding.pricePerUnit.text = StringBuilder()
                .append(productDetailBundle.productDetailUI.pricePerUnit)
                .append("/кг")
                .toString()
        }

        if (productDetailBundle.productDetailUI.commentsAmount != 0) {
            binding.commentAmount.text = StringBuilder()
                .append(productDetailBundle.productDetailUI.commentsAmount)
                .append(" | ")
                .append(" Читать отзывы")
                .toString()
        }

        if (productDetailBundle.productDetailUI.status != null) {
            binding.statusContainer.setCardBackgroundColor(Color.parseColor(productDetailBundle.productDetailUI.statusColor))
            binding.status.text = productDetailBundle.productDetailUI.status
            binding.statusContainer.visibility = View.VISIBLE
        } else {
            binding.statusContainer.visibility = View.GONE
        }

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
    }

    private fun fillCategoryAndBrandSection(productDetailBundleUI: ProductDetailBundleUI) {
        binding.categoryName.text = productDetailBundleUI.categoryUI.name
        Glide.with(requireContext())
            .load(productDetailBundleUI.categoryUI.detailPicture)
            .into(binding.categoryImage)

        when(productDetailBundleUI.productDetailUI.brandUI) {
            null -> {
                binding.brandContainer.visibility = View.GONE
                binding.dividerBetweenCategoryAndBrand.visibility = View.GONE
            }
            else -> {
                binding.brandContainer.visibility = View.VISIBLE
                binding.dividerBetweenCategoryAndBrand.visibility = View.VISIBLE
                binding.brandName.text = productDetailBundleUI.productDetailUI.brandUI.name
                Glide.with(requireContext())
                    .load(productDetailBundleUI.productDetailUI.brandUI.detailPicture)
                    .into(binding.brandImage)
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

    private fun fillPrice(productDetailUI: ProductDetailUI) {
        when(productDetailUI.priceUIList.size) {
            1 -> {
                binding.price.setPriceText(productDetailUI.priceUIList.first().price)
                binding.floatingPrice.setPriceText(productDetailUI.priceUIList.first().price)
                if (productDetailUI.priceUIList.first().oldPrice != 0) {
                    binding.price.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    binding.floatingPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    binding.oldPrice.setPriceText(productDetailUI.priceUIList.first().oldPrice)
                    binding.floatingOldPrice.setPriceText(productDetailUI.priceUIList.first().oldPrice)
                    binding.discount.setDiscountText(
                        oldPrice = productDetailUI.priceUIList.first().oldPrice,
                        newPrice = productDetailUI.priceUIList.first().price,
                    )
                    binding.discount.visibility = View.VISIBLE
                } else {
                    binding.discount.visibility = View.GONE
                }
            }
            else -> {
                binding.price.text = StringBuilder()
                    .append("от ")
                    .append(productDetailUI.priceUIList.last().price)
                    .append(" Р")
                binding.priceCondition.text = StringBuilder()
                    .append("при условии покупки от ")
                    .append(productDetailUI.priceUIList.last().requiredAmount)
                    .append(" шт")
                binding.priceCondition.visibility = View.VISIBLE
                pricesAdapter.priceUIList = productDetailUI.priceUIList
                pricesAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun fillCommentRecycler(commentUIList: List<CommentUI>) {
        binding.commentAmountTitle.text = StringBuilder()
            .append("Отзывы (")
            .append(commentUIList.size)
            .append(")")
            .toString()
        commentWithAvatarAdapter.commentUiList = commentUIList
        commentWithAvatarAdapter.notifyDataSetChanged()

        if (commentUIList.isEmpty()) {
            binding.commentTitleContainer.visibility = View.GONE
            binding.noCommentsTitle.visibility = View.VISIBLE
        }
    }

    private fun fillDetailPictureRecycler(detailPictureList: List<String>) {
        val diffUtil = DetailPictureDiffUtilCallback(
            oldList = detailPictureSliderAdapter.detailPictureUrlList,
            newList = detailPictureList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            detailPictureSliderAdapter.detailPictureUrlList = detailPictureList
            diffResult.dispatchUpdatesTo(detailPictureSliderAdapter)
        }
    }

    override fun onStart() {
        super.onStart()

        onProductClickSubject.subscribeBy { productId ->
            onProductClick(productId)
        }.addTo(compositeDisposable)

        onPromotionClickSubject.subscribeBy { promotionId ->
            findNavController().navigate(ProductDetailFragmentDirections.actionToPromotionDetailFragment(promotionId))
        }.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
        amountControllerTimer.cancel()
    }

    override fun onProductClick(productId: Long) {
        findNavController().navigate(ProductDetailFragmentDirections.actionToSelf(productId))
    }

    override fun onFavoriteClick(pair: Pair<Long, Boolean>) {

    }

}