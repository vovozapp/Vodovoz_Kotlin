package com.vodovoz.app.feature.cart

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.databinding.FragmentMainCartFlowBinding
import com.vodovoz.app.feature.cart.adapter.CartMainClickListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.replacement.ReplacementProductsSelectionBS
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.extensions.snack
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CartFragment : BaseFragment() {

    companion object {
        const val GIFT_ID = "GIFT_ID"
    }

    override fun layout(): Int = R.layout.fragment_main_cart_flow
    private val binding: FragmentMainCartFlowBinding by viewBinding {
        FragmentMainCartFlowBinding.bind(
            contentView
        )
    }
    private val viewModel: CartFlowViewModel by activityViewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    private val cartController by lazy {
        CartController(
            getCartMainClickListener(),
            getProductsClickListener(),
            cartManager,
            likeManager,
            ratingProductManager
        )
    }

    private fun getCartMainClickListener(): CartMainClickListener {
        return object : CartMainClickListener {
            override fun onSwapProduct(productUI: ProductUI) {
                findNavController().navigate(
                    CartFragmentDirections.actionToReplacementProductsSelectionBS(
                        productUI.detailPicture,
                        productUI.replacementProductUIList.toTypedArray(),
                        productUI.id,
                        productUI.name
                    ))
            }

            override fun onChooseBtnClick() {
                findNavController().navigate(CartFragmentDirections.actionToAllBottlesFragment())
            }

            override fun onApplyPromoCodeClick(code: String) {
                viewModel.fetchCart(code)
            }

            override fun onGoToCatalogClick() {
                findNavController().navigate(R.id.catalogFragment)
            }

        }
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                findNavController().navigate(CartFragmentDirections.actionToProductDetailFragment(id))
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                when (viewModel.isLoginAlready()) {
                    true -> findNavController().navigate(
                        CartFragmentDirections.actionToPreOrderBS(
                            id,
                            name,
                            detailPicture
                        )
                    )
                    false -> findNavController().navigate(CartFragmentDirections.actionToProfileFragment())
                }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.firstLoad()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUiState()

        bindButtons()
        initActionBar()
        observeResultLiveData()
        observeNavigates()
        cartController.bind(binding.mainRv)
        bindErrorRefresh { viewModel.refresh() }
        bindSwipeRefresh()
    }

    private fun bindSwipeRefresh() {
        binding.refreshContainer.setOnRefreshListener {
            viewModel.refresh()
            binding.refreshContainer.isRefreshing = false
        }
    }

    private fun initActionBar() {
        initToolbar(requireContext().getString(R.string.cart_title), addAction = true, showNavBtn = false)
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
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

                    if (cartState.data.infoMessage.isNotEmpty()) Snackbar.make(binding.root, cartState.data.infoMessage, Snackbar.LENGTH_LONG).show()

                    when(cartState.data.giftProductUIList.isEmpty()) {
                        true -> binding.bottom.llShowGifts.visibility = View.GONE
                        false -> binding.bottom.llShowGifts.visibility = View.VISIBLE
                    }

                    if (cartState.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    val availableItems = cartState.data.availableProducts?.items
                    val notAvailableItems = cartState.data.notAvailableProducts?.items

                    if (cartState.data.total != null) {
                        binding.bottom.btnRegOrderFlow.text = StringBuilder().append("Оформить заказ на ").append(cartState.data.total.prices.total).append(" ₽").toString()
                    }

                    if (availableItems != null) {
                        if (availableItems.isEmpty()) {
                            binding.bottom.root.isVisible = false
                            cartController.submitList(
                                listOfNotNull(
                                    cartState.data.cartEmpty,
                                    cartState.data.bestForYouProducts
                                )
                            )
                        } else {
                            binding.bottom.root.isVisible = true
                            cartController.submitList(
                                listOfNotNull(
                                    cartState.data.notAvailableProducts.takeIf { notAvailableItems.isNullOrEmpty().not() },
                                    cartState.data.availableProducts,
                                    cartState.data.total
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun observeNavigates() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeNavigateToOrder()
                .collect {
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
        }

        lifecycleScope.launchWhenStarted {
            viewModel.observeNavigateToGiftsBottom()
                .collect {
                    when (viewModel.isLoginAlready()) {
                        true -> {
                            if (findNavController().currentBackStackEntry?.destination?.id == R.id.giftsBottomFragment) {
                                findNavController().popBackStack()
                            }

                            findNavController().navigate(
                                CartFragmentDirections.actionToGiftsBottomFragment(
                                    it.toTypedArray()
                                ))
                        }
                        false -> {
                            if (findNavController().currentBackStackEntry?.destination?.id == R.id.giftsBottomFragment) {
                                findNavController().popBackStack()
                            }
                            findNavController().navigate(CartFragmentDirections.actionToProfileFragment())
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_cart_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clearCart -> clearCart()
            R.id.orderHistory -> {
                findNavController().navigate(CartFragmentDirections.actionToAllOrdersFragment())
            }
        }
        return false
    }

    private fun clearCart() {
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
                val oldQ = gift.cartQuantity
                gift.cartQuantity++
                viewModel.changeCart(gift.id, gift.cartQuantity, oldQ)
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

}
/*
@AndroidEntryPoint
class CartFragment : ViewStateBaseFragment() {

    companion object {
        const val GIFT_ID = "GIFT_ID"
    }

    private lateinit var binding: FragmentMainCartBinding
    private val viewModel: CartViewModel by viewModels()

    private val bestForYouProductsSliderFragment: ProductsSliderFragment by lazy {
        ProductsSliderFragment.newInstance(ProductsSliderConfig(
            containShowAllButton = false,
            largeTitle = true
        )) }

    private val availableCartItemsAdapter = LinearProductsAdapter(
        onProductClick = {
            findNavController().navigate(CartFragmentDirections.actionToProductDetailFragment(it))
        },
        onChangeFavoriteStatus = { productId, status ->
            viewModel.changeFavoriteStatus(productId, status)
        },
        onChangeCartQuantity = { productId, quantity ->
            viewModel.changeCart(productId, quantity)
        },
        onNotAvailableMore = {},
        onNotifyWhenBeAvailable = { id, name, picture ->
            findNavController().navigate(CartFragmentDirections.actionToPreOrderBS(
                id, name, picture
            ))
        },
        isCart = true
    )

    private val notAvailableCartItemsAdapter = NotAvailableCartItemsAdapter(
        onProductClick = { productId ->
            findNavController().navigate(CartFragmentDirections.actionToProductDetailFragment(productId))
        },
        onSwapProduct = { productId ->
            viewModel.notAvailableProductUIList.find { it.id == productId }?.let {
                findNavController().navigate(CartFragmentDirections.actionToReplacementProductsSelectionBS(
                    it.detailPicture,
                    it.replacementProductUIList.toTypedArray(),
                    it.id,
                    it.name
                ))
            }
        }
    )

    val space: Int by lazy { resources.getDimension(R.dimen.space_16).toInt() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_cart_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.clearCart -> clearCart()
            R.id.orderHistory -> {
                findNavController().navigate(CartFragmentDirections.actionToAllOrdersFragment())
            }
        }
        return false
    }

    private fun clearCart() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Очистить корзину?")
            .setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }
            .setPositiveButton(resources.getString(R.string.confirm)) { _, _ ->
                viewModel.clearCart()
            }.show()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentMainCartBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initActionBar()
        observeViewModel()
        observeResultLiveData()
        initAvailableProductRecycler()
        initNotAvailableProductRecycler()
        initBestForYouProductsSlider()
        initButtons()
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<ProductUI>(GIFT_ID)
            ?.observe(viewLifecycleOwner) { gift ->
                gift.cartQuantity++
                viewModel.changeCart(gift.id, gift.cartQuantity)
            }
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(ReplacementProductsSelectionBS.SELECTED_PRODUCT_ID)
            ?.observe(viewLifecycleOwner) { productId ->
                if (findNavController().currentDestination?.id == R.id.replacementProductsSelectionBS) {
                    findNavController().popBackStack()
                    findNavController().navigate(CartFragmentDirections.actionToProductDetailFragment(productId))
                }
            }
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Boolean>(ReplacementProductsSelectionBS.CHANGE_CART)
            ?.observe(viewLifecycleOwner) {
                if (it) viewModel.updateData()
            }
    }

    private fun initActionBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.incAppBar.tbToolbar)
        binding.incAppBar.tbToolbar.overflowIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_more_actions)
        binding.incAppBar.tvTitle.text = requireContext().getString(R.string.cart_title)
    }

    private fun initBestForYouProductsSlider() {
        childFragmentManager.beginTransaction().replace(
            R.id.fcvBestForYouProductSliderFragment,
            bestForYouProductsSliderFragment
        ).commit()

        bestForYouProductsSliderFragment.initCallbacks(
            iOnProductClick = { productId ->
                findNavController().navigate(ProfileFragmentDirections.actionToProductDetailFragment(productId))
            },
            iOnChangeProductQuantity = { pair -> viewModel.changeCart(pair.first, pair.second) },
            iOnFavoriteClick = { pair -> viewModel.changeFavoriteStatus(pair.first, pair.second) },
            iOnShowAllProductsClick = {},
            onNotAvailableMore = {},
            onNotifyWhenBeAvailable = { id, name, picture ->
                when(viewModel.isAlreadyLogin()) {
                    true -> findNavController().navigate(CartFragmentDirections.actionToPreOrderBS(id, name, picture))
                    false -> findNavController().navigate(CartFragmentDirections.actionToProfileFragment())
                }
            }
        )
    }

    private fun initButtons() {
        binding.btnGoToCatalog.setOnClickListener {
            findNavController().navigate(R.id.catalogFragment)
        }
        binding.llShowGifts.setOnClickListener {
            when (viewModel.isAlreadyLogin()) {
                true -> {
                    findNavController().navigate(CartFragmentDirections.actionToGiftsBottomFragment(
                        viewModel.getGiftList().toTypedArray()
                    ))
                }
                false -> findNavController().navigate(CartFragmentDirections.actionToProfileFragment())
            }
        }
        binding.btnRegOrder.setOnClickListener {
            findNavController().navigate(CartFragmentDirections.actionToOrderingFragment(
                viewModel.total, viewModel.discount, viewModel.deposit, viewModel.full, viewModel.getCart(), viewModel.coupon
            ))
        }
        binding.tvApplyPromoCode.setOnClickListener {
            viewModel.coupon = binding.etCoupon.text.toString()
            viewModel.updateData()
        }
    }

    private fun initAvailableProductRecycler() {
        binding.rvAvailableProductRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAvailableProductRecycler.adapter = availableCartItemsAdapter
        ContextCompat.getDrawable(requireContext(), R.drawable.bkg_gray_divider)?.let {
            binding.rvAvailableProductRecycler.addItemDecoration(Divider(it, space))
        }
        binding.cbReturnBottles.setOnCheckedChangeListener { _, isChecked ->
            when(isChecked) {
                true -> binding.rlChooseBottleBtnContainer.visibility = View.VISIBLE
                false -> binding.rlChooseBottleBtnContainer.visibility = View.GONE
            }
        }
        binding.btnChooseBottle.setOnClickListener { findNavController().navigate(CartFragmentDirections.actionToAllBottlesFragment()) }
        binding.rvAvailableProductRecycler.addMarginDecoration { rect, _, _, _ ->
            rect.top = space
            rect.bottom = space
            rect.right = space
        }
    }

    private fun initNotAvailableProductRecycler() {
        binding.rvNotAvailableProductRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotAvailableProductRecycler.adapter = notAvailableCartItemsAdapter
        ContextCompat.getDrawable(requireContext(), R.drawable.bkg_gray_divider)?.let {
            binding.rvNotAvailableProductRecycler.addItemDecoration(Divider(it, space/4))
        }
        binding.rvNotAvailableProductRecycler.addMarginDecoration { rect, _, _, _ ->
            rect.top = space/4
            rect.bottom = space/4
            rect.right = space
        }
    }

    override fun update() {
        when(viewModel.isTryToClearCart) {
            false -> viewModel.updateData()
            true -> viewModel.clearCart()
        }
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Loading -> onStateLoading()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Hide -> onStateHide()
                is ViewState.Success ->  onStateSuccess()
            }
        }

        viewModel.availableProductListLD.observe(viewLifecycleOwner) { productUIList ->
            when (productUIList.isEmpty()) {
                true -> {
                    binding.llEmptyCartContainer.visibility = View.VISIBLE
                    binding.rlCartContainer.visibility = View.INVISIBLE
                    binding.incAppBar.abAppBar.translationZ = 0f
                }
                false -> {
                    binding.llEmptyCartContainer.visibility = View.INVISIBLE
                    binding.rlCartContainer.visibility = View.VISIBLE
                    fillAvailableProductRecycler(productUIList)
                    binding.nsvCartScrollContainer.setScrollElevation(binding.incAppBar.abAppBar)
                }
            }
        }

        viewModel.notAvailableProductListLD.observe(viewLifecycleOwner) { productUIList ->
            fillNotAvailableProductRecycler(productUIList)
        }

        viewModel.errorLD.observe(viewLifecycleOwner) { error ->
            Snackbar.make(binding.root, error.toString(), Snackbar.LENGTH_SHORT).show()
        }

        viewModel.giftMessageLD.observe(viewLifecycleOwner) { giftMessage ->
            when(giftMessage) {
                null -> binding.tvGiftMessage.visibility = View.GONE
                else -> {
                    binding.tvGiftMessage.visibility = View.VISIBLE
                    binding.tvGiftMessage.text = giftMessage
                }
            }
        }

        viewModel.bestForYouCategoryDetailLD.observe(viewLifecycleOwner) { bestForYouCategoryDetailUI ->
            bestForYouProductsSliderFragment.updateData(listOf(bestForYouCategoryDetailUI))
        }

        viewModel.giftProductListLD.observe(viewLifecycleOwner) { giftList ->
            when(giftList.isEmpty()) {
                true -> binding.llShowGifts.visibility = View.GONE
                false -> binding.llShowGifts.visibility = View.VISIBLE
            }
        }

        viewModel.fullPriceLD.observe(viewLifecycleOwner) { binding.tvFullPrice.setPriceText(it) }
        viewModel.depositPriceLD.observe(viewLifecycleOwner) { binding.tvDepositPrice.setPriceText(it) }
        viewModel.discountPriceLD.observe(viewLifecycleOwner) { binding.tvDiscountPrice.setPriceText(it, true) }
        viewModel.totalPriceLD.observe(viewLifecycleOwner) {
            binding.tvTotalPrice.setPriceText(it)
            binding.btnRegOrder.text = StringBuilder().append("Оформить заказ на ").append(it).append(" ₽").toString()
        }
        viewModel.infoMessageLD.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fillAvailableProductRecycler(productUIList: List<ProductUI>) {
//        val diffUtil = ProductDiffUtilCallback(
//            oldList = availableCartItemsAdapter.productUIList,
//            newList = productUIList
//        )
//
//        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
//            availableCartItemsAdapter.productUIList = productUIList
//            diffResult.dispatchUpdatesTo(availableCartItemsAdapter)
//        }

        var isCanReturnBottles = false
        for (product in productUIList) {
            if (product.depositPrice != 0) {
                isCanReturnBottles = true
                break
            }
        }

        availableCartItemsAdapter.productUIList = productUIList
        availableCartItemsAdapter.notifyDataSetChanged()
        binding.rlChooseBottleBtnContainer.visibility = View.GONE
        when(isCanReturnBottles || binding.cbReturnBottles.isChecked) {
            true -> binding.llReturnBottlesContainer.visibility = View.VISIBLE
            false -> binding.llReturnBottlesContainer.visibility = View.GONE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fillNotAvailableProductRecycler(productUIList: List<ProductUI>) {
        when(productUIList.isEmpty()) {
            true -> binding.llNotAvailableProductsContainer.visibility = View.GONE
            false -> {
                binding.llNotAvailableProductsContainer.visibility = View.VISIBLE
                notAvailableCartItemsAdapter.productUIList = productUIList
                notAvailableCartItemsAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.isFirstUpdate = true
        viewModel.updateData()
    }

}*/
