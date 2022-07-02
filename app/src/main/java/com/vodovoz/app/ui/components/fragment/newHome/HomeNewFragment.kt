package com.vodovoz.app.ui.components.fragment.newHome

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.R
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.databinding.FragmentMainHomeBinding
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.ViewStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.fragment.home_bottom_info.BottomInfoFragment
import com.vodovoz.app.ui.components.fragment.slider.banners_slider.BannersSliderFragment
import com.vodovoz.app.ui.components.fragment.slider.brands_slider.BrandsSliderFragment
import com.vodovoz.app.ui.components.fragment.slider.comments_slider.CommentsSliderFragment
import com.vodovoz.app.ui.components.fragment.slider.countries_slider.CountriesSliderFragment
import com.vodovoz.app.ui.components.fragment.slider.histories_slider.HistorySliderFragment
import com.vodovoz.app.ui.components.fragment.slider.order_slider.OrderSliderFragment
import com.vodovoz.app.ui.components.fragment.slider.popular_slider.PopularCategoriesSliderFragment
import com.vodovoz.app.ui.components.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.components.fragment.slider.products_slider.ProductsSliderFragment
import com.vodovoz.app.ui.components.fragment.slider.promotion_slider.PromotionsSliderFragment
import com.vodovoz.app.ui.components.interfaces.*
import com.vodovoz.app.ui.extensions.BannerActionExtensions.invoke
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.sql.Date
import java.sql.Time

class HomeNewFragment : ViewStateBaseFragment(), IOnBannerClick, IOnProductClick,
    IOnChangeProductQuantity, IOnBrandClick, IOnCountryClick, IOnCommentClick, IOnHistoryClick,
    IOnPopularCategoryClick,
    IOnPromotionClick, IOnOrderClick {

    private lateinit var binding: FragmentMainHomeBinding
    private lateinit var viewModel: HomeNewViewModel

    private val compositeDisposable = CompositeDisposable()

    private val advertisingBannersSliderFragment: BannersSliderFragment by lazy {
        BannersSliderFragment.newInstance(bannerRatio = 0.41) }
    private val historiesSliderFragment: HistorySliderFragment by lazy { HistorySliderFragment() }
    private val popularCategoriesSliderFragment: PopularCategoriesSliderFragment by lazy { PopularCategoriesSliderFragment() }
    private val categoryBannersSliderFragment: BannersSliderFragment by lazy {
        BannersSliderFragment.newInstance(bannerRatio = 0.48) }
    private val discountProductsSliderFragment: ProductsSliderFragment by lazy {
        ProductsSliderFragment.newInstance(ProductsSliderConfig(
            containShowAllButton = true
        )) }
    private val topProductsSliderFragment: ProductsSliderFragment by lazy {
        ProductsSliderFragment.newInstance(ProductsSliderConfig(
            containShowAllButton = true
        )) }
    private val ordersSliderFragment: OrderSliderFragment by lazy { OrderSliderFragment() }
    private val noveltiesProductsSliderFragment: ProductsSliderFragment by lazy {
        ProductsSliderFragment.newInstance(ProductsSliderConfig(
            containShowAllButton = true
        )) }
    private val promotionsSliderFragment: PromotionsSliderFragment by lazy { PromotionsSliderFragment() }
    private val bottomProductsSliderFragment: ProductsSliderFragment by lazy {
        ProductsSliderFragment.newInstance(ProductsSliderConfig(
            containShowAllButton = true
        )) }
    private val brandsSliderFragment: BrandsSliderFragment by lazy { BrandsSliderFragment() }
    private val countriesSliderFragment: CountriesSliderFragment by lazy { CountriesSliderFragment() }
    private val viewedProductsSliderFragment: ProductsSliderFragment by lazy {
        ProductsSliderFragment.newInstance(ProductsSliderConfig(
            containShowAllButton = false
        )) }
    private val commentsSliderFragment: CommentsSliderFragment by lazy { CommentsSliderFragment() }
    private val bottomInfoFragment: BottomInfoFragment by lazy { BottomInfoFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[HomeNewViewModel::class.java]
        viewModel.updateData()
    }

    override fun update() { viewModel.updateData() }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentMainHomeBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        loadFragments()
        initCallbacks()
        initOther()
        observeViewModel()
    }

    private fun loadFragments() {
        childFragmentManager.beginTransaction().replace(
            R.id.advertisingBannersSliderFragment,
            advertisingBannersSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.historiesSliderFragment,
            historiesSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.popularCategoriesSliderFragment,
            popularCategoriesSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.discountProductsSliderFragment,
            discountProductsSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.categoryBannersSliderFragment,
            categoryBannersSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.topProductsSliderFragment,
            topProductsSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.ordersSliderFragment,
            ordersSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.noveltiesProductsSliderFragment,
            noveltiesProductsSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.promotionsSliderFragment,
            promotionsSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.bottomProductsSliderFragment,
            bottomProductsSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.brandsSliderFragment,
            brandsSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.countriesSliderFragment,
            countriesSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.viewedProductsSliderFragment,
            viewedProductsSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.commentsSliderFragment,
            commentsSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.commentsSliderFragment,
            commentsSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.bottomInfoFragment,
            bottomInfoFragment
        ).commit()
    }

    private fun initCallbacks() {
        advertisingBannersSliderFragment.initCallbacks(iOnBannerClick = this)
        historiesSliderFragment.initCallbacks(iOnHistoryClick = this)
        popularCategoriesSliderFragment.initCallbacks(iOnPopularCategoryClick = this)
        discountProductsSliderFragment.initCallbacks(
            iOnProductClick = this,
            iOnChangeProductQuantity = this,
            iOnShowAllProductsClick = {

            }
        )
        categoryBannersSliderFragment.initCallbacks( iOnBannerClick = this)
        topProductsSliderFragment.initCallbacks(
            iOnProductClick = this,
            iOnChangeProductQuantity = this,
            iOnShowAllProductsClick = {

            }
        )
        ordersSliderFragment.initCallbacks(
            iOnOrderClick = this,
            iOnShowAllOrdersClick = {}
        )
        noveltiesProductsSliderFragment.initCallbacks(
            iOnProductClick = this,
            iOnChangeProductQuantity = this,
            iOnShowAllProductsClick = {

            }
        )
        promotionsSliderFragment.initCallbacks(
            iOnPromotionClick = this,
            iOnProductClick = this,
            iOnShowAllPromotionsClick = {

            }
        )
        bottomProductsSliderFragment.initCallbacks(
            iOnProductClick = this,
            iOnChangeProductQuantity = this,
            iOnShowAllProductsClick = {

            }
        )
        brandsSliderFragment.initCallbacks(
            iOnBrandClick = this,
            iOnShowAllBrandsClick = {

            }
        )
        countriesSliderFragment.initCallbacks(iOnCountryClick = this)
        viewedProductsSliderFragment.initCallbacks(
            iOnProductClick = this,
            iOnChangeProductQuantity = this,
            iOnShowAllProductsClick = {

            }
        )
        commentsSliderFragment.initCallbacks(iOnCommentClick = this)
    }

    private fun initOther() {
        binding.refreshContainer.setOnRefreshListener {
            update()
        }
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Hide -> onStateHide()
                is ViewState.Loading -> onStateLoading()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Success -> {
                    onStateSuccess()
                    binding.refreshContainer.isRefreshing = false
                }
            }
        }

        viewModel.advertisingBannersSliderDataLD.observe(viewLifecycleOwner) { bannerUIList ->
            advertisingBannersSliderFragment.updateData(bannerUIList)
        }

        viewModel.advertisingBannersSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> advertisingBannersSliderFragment.hide()
                false -> advertisingBannersSliderFragment.show()
            }
        }

        viewModel.historiesSliderDataLD.observe(viewLifecycleOwner) { historyUIList ->
            historiesSliderFragment.updateData(historyUIList)
        }

        viewModel.historiesSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> historiesSliderFragment.hide()
                false -> historiesSliderFragment.show()
            }
        }

        viewModel.popularCategoriesSliderDataLD.observe(viewLifecycleOwner) { categoryUIList ->
            popularCategoriesSliderFragment.updateData(categoryUIList)
        }

        viewModel.popularCategoriesSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> popularCategoriesSliderFragment.hide()
                false -> popularCategoriesSliderFragment.show()
            }
        }

        viewModel.discountProductsSliderDataLD.observe(viewLifecycleOwner) { categoryDetailUIList ->
            discountProductsSliderFragment.updateData(categoryDetailUIList)
        }

        viewModel.discountProductsSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> discountProductsSliderFragment.hide()
                false -> discountProductsSliderFragment.show()
            }
        }

        viewModel.categoryBannersSliderDataLD.observe(viewLifecycleOwner) { bannerUIList ->
            categoryBannersSliderFragment.updateData(bannerUIList)
        }

        viewModel.categoryBannersSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> categoryBannersSliderFragment.hide()
                false -> categoryBannersSliderFragment.show()
            }
        }

        viewModel.topProductsSliderDataLD.observe(viewLifecycleOwner) { categoryDetailUIList ->
            topProductsSliderFragment.updateData(categoryDetailUIList)
        }

        viewModel.topProductsSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> topProductsSliderFragment.hide()
                false -> topProductsSliderFragment.show()
            }
        }

        viewModel.ordersSliderDataLD.observe(viewLifecycleOwner) { orderUIList ->
            ordersSliderFragment.updateData(orderUIList)
        }

        viewModel.ordersSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> ordersSliderFragment.hide()
                false -> ordersSliderFragment.show()
            }
        }

        viewModel.noveltiesProductsSliderDataLD.observe(viewLifecycleOwner) { categoryDetailUIList ->
            noveltiesProductsSliderFragment.updateData(categoryDetailUIList)
        }

        viewModel.noveltiesProductsSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> noveltiesProductsSliderFragment.hide()
                false -> noveltiesProductsSliderFragment.show()
            }
        }

        viewModel.promotionsSliderDataLD.observe(viewLifecycleOwner) { promotionsSliderBundleUI ->
            promotionsSliderFragment.updateData(promotionsSliderBundleUI)
        }

        viewModel.promotionsSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> promotionsSliderFragment.hide()
                false -> promotionsSliderFragment.show()
            }
        }

        viewModel.bottomProductsSliderDataLD.observe(viewLifecycleOwner) { categoryDetailUIList ->
            bottomProductsSliderFragment.updateData(categoryDetailUIList)
        }

        viewModel.bottomProductsSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> bottomProductsSliderFragment.hide()
                false -> bottomProductsSliderFragment.show()
            }
        }

        viewModel.brandsSliderDataLD.observe(viewLifecycleOwner) { brandUIList ->
            brandsSliderFragment.updateData(brandUIList)
        }

        viewModel.brandsSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> brandsSliderFragment.hide()
                false -> brandsSliderFragment.show()
            }
        }

        viewModel.countriesSliderDataLD.observe(viewLifecycleOwner) { countriesSliderBundleUI ->
            countriesSliderFragment.updateData(countriesSliderBundleUI)
        }

        viewModel.countriesSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> countriesSliderFragment.hide()
                false -> countriesSliderFragment.show()
            }
        }

        viewModel.viewedProductsSliderDataLD.observe(viewLifecycleOwner) { categoryDetailUIList ->
            viewedProductsSliderFragment.updateData(categoryDetailUIList)
        }

        viewModel.viewedProductsSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> viewedProductsSliderFragment.hide()
                false -> viewedProductsSliderFragment.show()
            }
        }

        viewModel.commentsSliderDataLD.observe(viewLifecycleOwner) { commentUIList ->
            commentsSliderFragment.updateData(commentUIList)
        }

        viewModel.commentsSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> commentsSliderFragment.hide()
                false -> commentsSliderFragment.show()
            }
        }
    }

    override fun onBannerClick(actionEntity: ActionEntity) {
        actionEntity.invoke(findNavController(), requireActivity())
    }

    override fun onProductClick(productId: Long) {

    }

    override fun onChangeProductQuantity(productId: Long, quantity: Int) {

    }

    override fun onBrandClick(brandId: Long) {

    }

    override fun onCountryClick(countryId: Long) {

    }

    override fun onCommentClick(commentId: Long) {

    }

    override fun onHistoryClick(historyId: Long) {

    }

    override fun onPopularCategoryClick(categoryId: Long) {

    }

    override fun onPromotionClick(promotionId: Long) {

    }

    override fun onOrderClick(orderId: Long) {

    }
}