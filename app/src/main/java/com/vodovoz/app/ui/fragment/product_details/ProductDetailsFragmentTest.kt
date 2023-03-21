package com.vodovoz.app.ui.fragment.product_details

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentProductDetailsBinding
import com.vodovoz.app.ui.adapter.*
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.decoration.CommentMarginDecoration
import com.vodovoz.app.ui.decoration.PriceMarginDecoration
import com.vodovoz.app.ui.decoration.SearchMarginDecoration
import com.vodovoz.app.ui.diffUtils.DetailPictureDiffUtilCallback
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setCommentQuantity
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setDiscountPercent
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setMinimalPriceText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceCondition
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPricePerUnitText
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.extensions.ViewExtensions.onRenderFinished
import com.vodovoz.app.ui.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.fragment.product_info.ProductInfoFragment
import com.vodovoz.app.ui.fragment.product_properties.ProductPropertiesFragment
import com.vodovoz.app.ui.fragment.replacement_product.ReplacementProductsSelectionBS
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderFragment
import com.vodovoz.app.ui.fragment.slider.promotion_slider.PromotionsSliderFragment
import com.vodovoz.app.ui.fragment.some_products_by_brand.SomeProductsByBrandFragment
import com.vodovoz.app.ui.fragment.some_products_maybe_like.SomeProductsMaybeLikeFragment
import com.vodovoz.app.ui.model.*
import com.vodovoz.app.ui.model.custom.ProductDetailBundleUI
import com.vodovoz.app.ui.model.custom.PromotionsSliderBundleUI
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

@SuppressLint("NotifyDataSetChanged")
@AndroidEntryPoint
class ProductDetailsFragmentTest : ViewStateBaseFragment() {

    private lateinit var binding: FragmentProductDetailsBinding
    private val viewModel: ProductDetailsViewModel by viewModels()

    private val compositeDisposable = CompositeDisposable()

    private val onServiceClickSubject: PublishSubject<String> = PublishSubject.create()
    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onQueryClickSubject: PublishSubject<String> = PublishSubject.create()

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

    private val promotionsSliderFragment: PromotionsSliderFragment by lazy { PromotionsSliderFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeSubjects()
    }


    private fun subscribeSubjects() {
        onQueryClickSubject.subscribeBy { query ->
            findNavController().navigate(ProductDetailsFragmentDirections.actionToSearchFragment(query))
        }.addTo(compositeDisposable)
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
        initPriceRecycler()

        initAboutProductPager()
        initCommentRecycler()
        initSearchWordRecycler()
        initButtons()
        observeResultLiveData()
    }

    override fun update() {
        viewModel.updateProductDetail()
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

    private fun initSearchWordRecycler() {
        binding.rvSearchWords.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvSearchWords.adapter = searchWordAdapter
        binding.rvSearchWords.addItemDecoration(SearchMarginDecoration(space/2))
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

    private fun fillView(productDetailBundleUI: ProductDetailBundleUI) {
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

    //    byWithProductsSliderFragment.updateData(listOf(categoryDetailUI))
    }

    private fun fillRecommendProductSliderFragment(categoryDetailUI: CategoryDetailUI) {
        when(categoryDetailUI.productUIList.isEmpty()) {
            true -> binding.recommendProductSliderFragment.visibility = View.GONE
            false -> binding.recommendProductSliderFragment.visibility = View.VISIBLE
        }

    //    recommendProductsSliderFragment.updateData(listOf(categoryDetailUI))
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

}