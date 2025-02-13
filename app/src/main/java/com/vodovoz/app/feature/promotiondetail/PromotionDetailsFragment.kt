package com.vodovoz.app.feature.promotiondetail

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.common.speechrecognizer.SpeechDialogFragment
import com.vodovoz.app.data.parser.response.promotion.PromotionDetailResponseJsonParser
import com.vodovoz.app.databinding.FragmentPromotionDetailFlowBinding
import com.vodovoz.app.feature.favorite.bestforyouadapter.BestForYouController
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.PromotionsClickListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.inneradapter.HomePromotionsInnerAdapter
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.model.PromotionDetailUI
import com.vodovoz.app.util.extensions.fromHtml
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PromotionDetailsFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_promotion_detail_flow

    private val binding: FragmentPromotionDetailFlowBinding by viewBinding {
        FragmentPromotionDetailFlowBinding.bind(
            contentView
        )
    }

    internal val viewModel: PromotionDetailFlowViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    private val bestForYouController by lazy {
        BestForYouController(
            cartManager,
            likeManager,
            getProductsShowClickListener(),
            getProductsClickListener()
        )
    }

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

    private val homePromotionsAdapter by lazy {
        HomePromotionsInnerAdapter(
            getProductsClickListener(),
            getPromotionsClickListener(),
            cartManager,
            likeManager
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.firstLoadSorted()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productsController.bind(binding.productRecycler)
        bestForYouController.bind(binding.fcvProductSliderRv)
        binding.vpPromotions.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpPromotions.adapter = homePromotionsAdapter

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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeUiState()
                    .collect { state ->

                        showLoaderWithBg(state.loadingPage)
                        binding.timeLeftContainer.isVisible = !state.loadingPage
                        binding.customerCategoryCard.isVisible = !state.loadingPage
                        binding.promotionProductsTitle.isVisible =!state.loadingPage

                        bindHeader(state.data.items)
                        bindErrorHeader(state.data.errorItem)

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
                            val homeTitle = HomeTitle(
                                id = 1,
                                type = HomeTitle.VIEWED_TITLE,
                                name = "Лучшее для вас",
                                showAll = false,
                                showAllName = "СМ.ВСЕ",
                                categoryProductsName = state.data.items.forYouCategoryDetailUI.name
                            )
                            bestForYouController.submitList(listOf(homeTitle, homeProducts))
                        }

                        state.data.items?.promotionCategoryDetailUI?.let {
                            binding.promotionProductsTitle.visibility = View.VISIBLE
                            binding.productRecycler.visibility = View.VISIBLE
                            productsController.submitList(state.data.items.promotionCategoryDetailUI.productUIList)
                        } ?: {
                            binding.promotionProductsTitle.visibility = View.GONE
                            binding.productRecycler.visibility = View.GONE
                        }

                        showError(state.error)

                    }
            }
        }
    }

    private fun bindHeader(promotionDetailUI: PromotionDetailUI?) {
        if (promotionDetailUI == null) return
        binding.contentContainer.isVisible = true
        binding.vpPromotions.isVisible = false
        binding.errorMesTitle.isVisible = false
        binding.errorMesDesc.isVisible = false
        binding.errorBottomMess.isVisible = false
        initToolbar(titleText = promotionDetailUI.name)
        binding.customerCategoryCard.setCardBackgroundColor(Color.parseColor(promotionDetailUI.statusColor))
        binding.customerCategory.text = promotionDetailUI.status
        binding.timeLeft.text = promotionDetailUI.timeLeft
        binding.detail.text = promotionDetailUI.detailText.fromHtml()
        Glide.with(requireContext())
            .load(promotionDetailUI.detailPicture)
            .into(binding.image)
        binding.rootView.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dark_white
            )
        )
    }

    private fun bindErrorHeader(promotionDetailErrorUI: PromotionDetailResponseJsonParser.PromotionDetailErrorUI?) {
        if (promotionDetailErrorUI == null) return

        initSearchToolbar(
            "Поиск товара",
            { findNavController().navigate(PromotionDetailsFragmentDirections.actionToSearchFragment()) },
            { findNavController().navigate(PromotionDetailsFragmentDirections.actionToSearchFragment()) },
            { navigateToQrCodeFragment() },
            { startSpeechRecognizer() },
            showBackBtn = true
        )
        binding.errorMesTitle.isVisible = true
        binding.errorMesDesc.isVisible = true
        binding.errorBottomMess.isVisible = true
        binding.errorMesTitle.text = promotionDetailErrorUI.title
        binding.errorMesDesc.text = promotionDetailErrorUI.message
        binding.errorBottomMess.text = promotionDetailErrorUI.bottomMessage
        binding.contentContainer.isVisible = false
        binding.vpPromotions.isVisible = true
        homePromotionsAdapter.submitList(promotionDetailErrorUI.promotionUIList)
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

    private fun getProductsShowClickListener(): ProductsShowAllListener {
        return object : ProductsShowAllListener {
            override fun showAllDiscountProducts(id: Long) {}
            override fun showAllTopProducts(id: Long) {}
            override fun showAllNoveltiesProducts(id: Long) {}
            override fun showAllBottomProducts(id: Long) {}
        }
    }

    private fun getPromotionsClickListener(): PromotionsClickListener {
        return object : PromotionsClickListener {
            override fun onPromotionClick(id: Long) {
                findNavController().navigate(
                    R.id.promotionDetailFragment, bundleOf("promotionId" to id)
                )
            }

            override fun onShowAllPromotionsClick() {}
        }
    }

    @Inject
    lateinit var permissionsControllerFactory: PermissionsController.Factory
    private val permissionsController by lazy { permissionsControllerFactory.create(requireActivity()) }

    private fun navigateToQrCodeFragment() {
        permissionsController.methodRequiresCameraPermission {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@methodRequiresCameraPermission
            }

            findNavController().navigate(R.id.qrCodeFragment)

        }
    }

    private fun startSpeechRecognizer() {
        permissionsController.methodRequiresRecordAudioPermission {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@methodRequiresRecordAudioPermission
            }

            SpeechDialogFragment().show(childFragmentManager, "TAG")
        }
    }

}