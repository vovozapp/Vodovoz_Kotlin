package com.vodovoz.app.feature.cart

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.databinding.FragmentMainCartFlowBinding
import com.vodovoz.app.feature.cart.adapter.CartMainClickListener
import com.vodovoz.app.feature.cart.menu.CartMenuListener
import com.vodovoz.app.feature.cart.menu.CartMenuProvider
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.replacement.ReplacementProductsSelectionBS
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.extensions.fromHtml
import com.vodovoz.app.util.extensions.getColorWithAlpha
import com.vodovoz.app.util.extensions.snack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class CartFragment : BaseFragment() {

    companion object {
        const val GIFT_ID = "GIFT_ID"
    }

    override fun layout(): Int = R.layout.fragment_main_cart_flow
    internal val binding: FragmentMainCartFlowBinding by viewBinding {
        FragmentMainCartFlowBinding.bind(
            contentView
        )
    }
    internal val viewModel: CartFlowViewModel by activityViewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    @Inject
    lateinit var tabManager: TabManager

    @Inject
    lateinit var accountManager: AccountManager

    private val cartController by lazy {
        CartController(
            getCartMainClickListener(),
            getProductsClickListener(),
            cartManager,
            likeManager,
            ratingProductManager
        )
    }

    private var topSnackTimer: CountDownTimer? = null

    private fun getCartMainClickListener(): CartMainClickListener {
        return object : CartMainClickListener {
            override fun onSwapProduct(productUI: ProductUI) {
                findNavController().navigate(
                    CartFragmentDirections.actionToReplacementProductsSelectionBS(
                        productUI.detailPicture,
                        productUI.replacementProductUIList.toTypedArray(),
                        productUI.id,
                        productUI.name
                    )
                )
            }

            override fun onChooseBtnClick() {
                findNavController().navigate(CartFragmentDirections.actionToAllBottlesFragment())
            }

            override fun onApplyPromoCodeClick(code: String) {
                viewModel.fetchCart(code)
            }

            override fun onGoToCatalogClick() {
                tabManager.selectTab(R.id.graph_catalog)
            }

            override fun showSnack(message: String) {
                requireActivity().snack(message)
            }

        }
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                findNavController().navigate(CartFragmentDirections.actionToProductDetailFragment(id))
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                viewModel.onPreOrderClick(id, name, detailPicture)
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

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        viewModel.firstLoad()
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewModel.refresh()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUiState()
        bindButtons()
        initActionBar()
        observeEvents()
        observeTabReselect()
        cartController.bind(binding.mainRv)
        bindErrorRefresh { viewModel.refresh() }
        bindSwipeRefresh()
        observeResultLiveData()
        accountManager.reportYandexMetrica("Зашел в корзину")
    }

    override fun onStart() {
        super.onStart()
        shineAnimation()
    }

    private fun shineAnimation() {
        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.left_right)
        binding.bottom.shine.startAnimation(anim)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(p0: Animation?) {
                binding.bottom.shine.startAnimation(anim)
            }

            override fun onAnimationStart(p0: Animation?) {}
            override fun onAnimationRepeat(p0: Animation?) {}
        })
    }

    private fun bindSwipeRefresh() {
        binding.refreshContainer.setOnRefreshListener {
            viewModel.refresh()
            binding.refreshContainer.isRefreshing = false
        }
    }

    private fun initActionBar() {
        initToolbar(
            requireContext().getString(R.string.cart_title),
            addAction = true,
            showNavBtn = false,
            provider = CartMenuProvider(getCartMenuListener())
        )
    }

    private fun getCartMenuListener(): CartMenuListener {
        return object : CartMenuListener {
            override fun onClearCartClick() {
                clearCart()
            }

            override fun onHistoryClick() {
                findNavController().navigate(CartFragmentDirections.actionToAllOrdersFragment())
            }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeUiState()
                    .collect { cartState ->

                        if (cartState.error != null) {
                            binding.bottom.root.isVisible = false
                            binding.mainRv.isVisible = false
                        } else {
                            binding.bottom.root.isVisible = true
                            binding.mainRv.isVisible = true
                        }

                        showError(cartState.error)

                        if (cartState.data.infoMessage?.message?.isNotEmpty() == true) {
                            showTopSnack(
                                cartState.data.infoMessage.message.fromHtml(),
                                cartState.data.infoMessage.color
                            )
                            viewModel.clearInfoMessage()
//                            requireActivity().snackTop(cartState.data.infoMessage.message)
                        }

                        if (cartState.data.giftMessageBottom?.message?.isEmpty() == true) {
                            binding.bottom.tvGiftMessage.visibility = View.GONE
                        } else {
                            binding.bottom.tvGiftMessage.visibility = View.VISIBLE
                            binding.bottom.yourGiftsTv.text =
                                cartState.data.giftMessageBottom?.title
                            binding.bottom.tvGiftMessage.text =
                                cartState.data.giftMessageBottom?.message?.fromHtml()
                        }

                        binding.bottom.llShowGifts.visibility =
                            if (cartState.data.giftProductUI?.productsList?.isEmpty() == true) {
                                View.GONE
                            } else {
                                View.VISIBLE
                            }

                        showLoaderWithBg(cartState.loadingPage)
//                        if (cartState.loadingPage) {
//                            showLoader()
//                        } else {
//                            hideLoader()
//                        }

                        val availableItems = cartState.data.availableProducts?.items
                        val notAvailableItems = cartState.data.notAvailableProducts?.items

                        if (cartState.data.total != null) {
                            binding.bottom.btnRegOrderFlow.text =
                                StringBuilder().append("Оформить заказ на ")
                                    .append(cartState.data.total.prices.total).append(" ₽")
                                    .toString()
                        }

                        showHideDefToolbarItem(R.id.clearCart, availableItems.isNullOrEmpty().not())

                        if (availableItems != null) {
                            if (availableItems.isEmpty()) {
                                binding.bottom.root.isVisible = false
                                cartController.submitList(
                                    listOfNotNull(
                                        cartState.data.cartEmpty,
                                        cartState.data.bestForYouTitle,
                                        cartState.data.bestForYouProducts
                                    )
                                )
                            } else {
                                binding.bottom.root.isVisible = true
                                cartController.submitList(
                                    listOfNotNull(
                                        cartState.data.notAvailableProducts.takeIf {
                                            notAvailableItems.isNullOrEmpty().not()
                                        },
                                        cartState.data.availableProducts,
                                        cartState.data.total
                                    )
                                )
                            }
                        }
                    }
            }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeEvent()
                    .collect {
                        when (it) {
                            is CartFlowViewModel.CartEvents.NavigateToOrder -> {
                                if (it.prices != null) {
                                    if (findNavController().currentBackStackEntry?.destination?.id == R.id.orderingFragment) {
                                        findNavController().popBackStack()
                                    }
                                    findNavController().navigate(
                                        CartFragmentDirections.actionToOrderingFragment(
                                            it.prices.total,
                                            it.prices.discountPrice,
                                            it.prices.deposit,
                                            it.prices.fullPrice,
                                            it.cart,
                                            it.coupon
                                        )
                                    )
                                }
                            }

                            is CartFlowViewModel.CartEvents.NavigateToGifts -> {
                                if (findNavController().currentBackStackEntry?.destination?.id == R.id.giftsBottomFragment) {
                                    findNavController().popBackStack()
                                }

                                findNavController().navigate(
                                    CartFragmentDirections.actionToGiftsBottomFragment(
                                        it.giftProducts
                                    )
                                )
                            }

                            is CartFlowViewModel.CartEvents.NavigateToProfile -> {
                                tabManager.setAuthRedirect(findNavController().graph.id)
                                tabManager.selectTab(R.id.graph_profile)
                            }

                            is CartFlowViewModel.CartEvents.GoToPreOrder -> {
                                if (findNavController().currentBackStackEntry?.destination?.id == R.id.preOrderBS) {
                                    findNavController().popBackStack()
                                }
                                findNavController().navigate(
                                    CartFragmentDirections.actionToPreOrderBS(
                                        it.id,
                                        it.name,
                                        it.detailPicture
                                    )
                                )
                            }
                        }
                    }
            }
        }
    }

    private fun bindButtons() {
        binding.bottom.llShowGifts.setOnClickListener {
            viewModel.navigateToGiftsBottomFragment()
        }
        binding.bottom.btnRegOrderFlow.setOnClickListener {
            viewModel.navigateToOrderFragment()
        }
    }

    internal fun clearCart() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Очистить корзину?")
            .setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }
            .setPositiveButton(resources.getString(R.string.confirm)) { _, _ ->
                viewModel.clearCart()
            }.show()
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<ProductUI>(GIFT_ID)
            ?.observe(viewLifecycleOwner) { gift ->
                if (gift == null) return@observe
                val oldQ = gift.cartQuantity
                gift.cartQuantity++
                viewModel.changeCart(gift.id, gift.cartQuantity, oldQ)
                findNavController().currentBackStackEntry?.savedStateHandle?.set(GIFT_ID, null)
                findNavController().previousBackStackEntry?.savedStateHandle?.set(GIFT_ID, null)
            }
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(ReplacementProductsSelectionBS.SELECTED_PRODUCT_ID)
            ?.observe(viewLifecycleOwner) { productId ->
                if (findNavController().currentDestination?.id == R.id.replacementProductsSelectionBS) {
                    findNavController().popBackStack()
                    findNavController().navigate(
                        CartFragmentDirections.actionToProductDetailFragment(
                            productId
                        )
                    )
                }
            }
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Boolean>(ReplacementProductsSelectionBS.CHANGE_CART)
            ?.observe(viewLifecycleOwner) {
                if (it) viewModel.fetchCart()
            }
    }

    private fun observeTabReselect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                tabManager.observeTabReselect()
                    .collect {
                        if (it != TabManager.DEFAULT_STATE && it == R.id.cartFragment) {
                            binding.mainRv.post {
                                binding.mainRv.smoothScrollToPosition(0)
                            }
                            tabManager.setDefaultState()
                        }
                    }
            }
        }
    }

    private fun showTopSnack(fromHtml: Spanned, color: String?) {
        binding.topSnack.visibility = View.VISIBLE
        binding.txtTopSnack.text = fromHtml
        val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(color?.getColorWithAlpha() ?: 0, 0x00000000, 0x00000000)
        )
        gd.setCornerRadius(0f)
        binding.topSnack.background = gd
        topSnackTimer?.cancel()
        topSnackTimer = object : CountDownTimer(3000, 3000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                binding.topSnack.visibility = View.GONE
            }
        }
        topSnackTimer?.start()
    }

    override fun onDestroyView() {
        viewModel.clearCoupon()
        super.onDestroyView()
    }

}