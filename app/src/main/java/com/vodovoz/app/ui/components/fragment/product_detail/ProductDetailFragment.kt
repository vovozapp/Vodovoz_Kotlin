package com.vodovoz.app.ui.components.fragment.product_detail

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentProductDetailBinding
import com.vodovoz.app.ui.components.adapter.CommentsWithAvatarAdapter
import com.vodovoz.app.ui.components.adapter.PricesAdapter
import com.vodovoz.app.ui.components.adapter.SearchWordsAdapter
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.ViewStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.base.picturePagerAdapter.DetailPictureDiffUtilCallback
import com.vodovoz.app.ui.components.base.picturePagerAdapter.DetailPictureSliderAdapter
import com.vodovoz.app.ui.components.decoration.CommentMarginDecoration
import com.vodovoz.app.ui.components.decoration.PriceMarginDecoration
import com.vodovoz.app.ui.components.decoration.SearchMarginDecoration
import com.vodovoz.app.ui.components.fragment.product_info.ProductInfoFragment
import com.vodovoz.app.ui.components.fragment.product_properties.ProductPropertiesFragment
import com.vodovoz.app.ui.components.fragment.slider.product_slider.ProductSliderFragment
import com.vodovoz.app.ui.components.fragment.slider.promotion_slider.PromotionSliderFragment
import com.vodovoz.app.ui.components.fragment.some_products_by_brand.SomeProductsByBrandFragment
import com.vodovoz.app.ui.components.fragment.some_products_maybe_like.SomeProductsMaybeLikeFragment
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setDiscountText
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.*
import com.vodovoz.app.ui.model.custom.ProductDetailBundleUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

@SuppressLint("NotifyDataSetChanged")
class ProductDetailFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentProductDetailBinding
    private lateinit var viewModel: ProductDetailViewModel

    private val compositeDisposable = CompositeDisposable()

    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onPromotionClickSubject: PublishSubject<Long> = PublishSubject.create()

    private val detailPictureSliderAdapter = DetailPictureSliderAdapter()
    private val pricesAdapter = PricesAdapter()
    private val commentWithAvatarAdapter = CommentsWithAvatarAdapter()
    private val searchWordAvatarAdapter = SearchWordsAdapter()

    private var aboutProductFragment: ProductInfoFragment? = null
    private var propertiesFragment: ProductPropertiesFragment? = null

    private val space: Int by lazy { resources.getDimension(R.dimen.primary_space).toInt() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        binding.showAllComment.setOnClickListener {  }
    }

    private fun initSearchWordRecycler() {
        binding.searchWordRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.searchWordRecycler.adapter = searchWordAvatarAdapter
        binding.searchWordRecycler.addItemDecoration(SearchMarginDecoration(space))
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
            true -> binding.promotionSliderFragment.visibility = View.GONE
            false -> binding.promotionSliderFragment.visibility = View.VISIBLE
        }

        childFragmentManager.beginTransaction()
            .replace(
                R.id.promotionSliderFragment,
                PromotionSliderFragment.newInstance(
                    PromotionSliderFragment.DataSource.Args(
                        title = "Товар учавствующий в акции",
                        promotionUIList = promotionUIList,
                    ),
                    onProductClickSubject = onProductClickSubject,
                    onPromotionClickSubject = onPromotionClickSubject
                )
            )
            .commit()
    }

    private fun fillByWithProductSlider(categoryDetailUI: CategoryDetailUI) {
        when(categoryDetailUI.productUIList.isEmpty()) {
            true -> binding.byWithProductSliderFragment.visibility = View.GONE
            false -> binding.byWithProductSliderFragment.visibility = View.VISIBLE
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.byWithProductSliderFragment, ProductSliderFragment.newInstance(
                dataSource = ProductSliderFragment.DataSource.Args(listOf(categoryDetailUI)),
                config = ProductSliderFragment.Config(true),
                onProductClickSubject = onProductClickSubject
            )).commit()
    }

    private fun fillRecommendProductSliderFragment(categoryDetailUI: CategoryDetailUI) {
        when(categoryDetailUI.productUIList.isEmpty()) {
            true -> binding.recommendProductSliderFragment.visibility = View.GONE
            false -> binding.recommendProductSliderFragment.visibility = View.VISIBLE
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.recommendProductSliderFragment, ProductSliderFragment.newInstance(
                dataSource = ProductSliderFragment.DataSource.Args(listOf(categoryDetailUI)),
                config = ProductSliderFragment.Config(true),
                onProductClickSubject = onProductClickSubject
            )).commit()
    }

    private fun fillPrice(productDetailUI: ProductDetailUI) {
        when(productDetailUI.priceUIList.size) {
            1 -> {
                binding.price.setPriceText(productDetailUI.priceUIList.first().price)
                if (productDetailUI.priceUIList.first().oldPrice != 0) {
                    binding.price.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    binding.oldPrice.setPriceText(productDetailUI.priceUIList.first().oldPrice)
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
            findNavController().navigate(ProductDetailFragmentDirections.actionToSelf(productId))
        }.addTo(compositeDisposable)

        onPromotionClickSubject.subscribeBy { promotionId ->
            findNavController().navigate(ProductDetailFragmentDirections.actionToPromotionDetailFragment(promotionId))
        }.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

}