package com.vodovoz.app.feature.profile

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.databinding.FragmentProfileFlowBinding
import com.vodovoz.app.feature.cart.CartFlowViewModel
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel
import com.vodovoz.app.feature.home.HomeFlowViewModel
import com.vodovoz.app.feature.home.viewholders.homeorders.inneradapter.HomeOrdersSliderClickListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.profile.adapter.ProfileFlowClickListener

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_profile_flow

    private val binding: FragmentProfileFlowBinding by viewBinding {
        FragmentProfileFlowBinding.bind(
            contentView
        )
    }

    private val viewModel: ProfileFlowViewModel by activityViewModels()
    private val flowViewModel: HomeFlowViewModel by activityViewModels()
    private val cartFlowViewModel: CartFlowViewModel by activityViewModels()
    private val favoriteViewModel: FavoriteFlowViewModel by activityViewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var tabManager: TabManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    private val profileController by lazy {
        ProfileFlowController(
            viewModel = viewModel,
            cartManager = cartManager,
            likeManager = likeManager,
            ratingProductManager = ratingProductManager,
            listener = getProfileFlowClickListener(),
            productsShowAllListener = getProductsShowClickListener(),
            productsClickListener = getProductsClickListener(),
            homeOrdersSliderClickListener = getHomeOrdersSliderClickListener()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoad()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileController.bind(binding.profileFlowRv, binding.refreshContainer)

        bindErrorRefresh { viewModel.refresh() }
        observeUiState()
        observeEvents()
        bindRegOrLoginBtn()
        observeTabReselect()
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkLogin()
    }

    private fun bindRegOrLoginBtn() {
        binding.btnLoginOrReg.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionToLoginFragment())
        }
    }

    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeEvent()
                .collect {
                    when(it) {
                        is ProfileFlowViewModel.ProfileEvents.Logout -> {
                            binding.profileFlowRv.isVisible = false
                            binding.noLoginContainer.isVisible = true

                            flowViewModel.refresh()
                            cartFlowViewModel.refreshIdle()
                            favoriteViewModel.refreshIdle()
                        }
                    }
                }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect { profileState ->

                    if (profileState.data.isLogin) {
                        if (profileState.data.items.mapNotNull { it.item }.size < 4) {
                            binding.profileFlowRv.isVisible = false
                            showLoaderWithBg(true)
                        } else {
                            binding.profileFlowRv.isVisible = true
                            showLoaderWithBg(false)
                        }
                    } else {
                        binding.profileFlowRv.isVisible = false
                        binding.noLoginContainer.isVisible = true
                    }

                    if (profileState.data.items.size in (ProfileFlowViewModel.POSITIONS_COUNT - 2..ProfileFlowViewModel.POSITIONS_COUNT)) {
                        val list =
                            profileState.data.items.sortedBy { it.position }.mapNotNull { it.item }
                        profileController.submitList(list)
                    }

                    showError(profileState.error)

                }
        }
    }

    private fun getProfileFlowClickListener() : ProfileFlowClickListener {
        return object : ProfileFlowClickListener {
            override fun onHeaderClick() {
                findNavController().navigate(ProfileFragmentDirections.actionToUserDataFragment())
            }

            override fun logout() {
                viewModel.logout()
            }

            override fun onAddressesClick() {
                findNavController().navigate(ProfileFragmentDirections.actionToSavedAddressesDialogFragment())
            }

            override fun onUrlClick() {
                findNavController().navigate(ProfileFragmentDirections.actionToWebViewFragment(
                    ApiConfig.ABOUT_DELIVERY_URL,
                    "Стоимость доставки"
                ))
            }

            override fun onOrdersHistoryClick() {
                findNavController().navigate(ProfileFragmentDirections.actionToAllOrdersFragment())
            }

            override fun onLastPurchasesClick() {
                findNavController().navigate(ProfileFragmentDirections.actionToPastPurchasesFragment())
            }

            override fun onRepairClick() {
                findNavController().navigate(ProfileFragmentDirections.actionToServiceDetailFragment(
                    listOf("sanitar", "remont").toTypedArray(),
                    "remont"
                ))
            }

            override fun onOzonClick() {
                findNavController().navigate(ProfileFragmentDirections.actionToServiceDetailFragment(
                    listOf("sanitar", "remont").toTypedArray(),
                    "sanitar"
                ))
            }

            override fun onQuestionnaireClick() {
                findNavController().navigate(ProfileFragmentDirections.actionToQuestionnairesFragment())
            }

            override fun onNotificationsClick() {
                //todo
            }

            override fun onAboutDeliveryClick() {
                findNavController().navigate(ProfileFragmentDirections.actionToWebViewFragment(
                    ApiConfig.ABOUT_DELIVERY_URL,
                    "Стоимость доставки"
                ))
            }

            override fun onAboutPaymentClick() {
                findNavController().navigate(ProfileFragmentDirections.actionToWebViewFragment(
                    ApiConfig.ABOUT_PAY_URL,
                    "Об оплате"
                ))
            }

            override fun onMyChatClick() {
                //todo
            }

            override fun onSafetyClick() {
                //todo
            }

            override fun onAboutAppClick() {
                findNavController().navigate(ProfileFragmentDirections.actionToAboutAppDialogFragment())
            }

        }
    }

    private fun getProductsShowClickListener(): ProductsShowAllListener {
        return object : ProductsShowAllListener {}
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                findNavController().navigate(ProfileFragmentDirections.actionToProductDetailFragment(id))
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                findNavController().navigate(ProfileFragmentDirections.actionToPreOrderBS(
                    id, name, detailPicture
                ))
            }

            override fun onChangeProductQuantity(id: Long, cartQuantity: Int, oldQuantity: Int) {
                viewModel.changeCart(id, cartQuantity, oldQuantity)
            }

            override fun onFavoriteClick(id: Long, isFavorite: Boolean) {
                viewModel.changeFavoriteStatus(id, isFavorite)
            }

            override fun onChangeRating(id: Long, rating: Float, oldRating: Float) {

            }
        }
    }

    private fun getHomeOrdersSliderClickListener() : HomeOrdersSliderClickListener {
        return object : HomeOrdersSliderClickListener {
            override fun onOrderClick(id: Long?) {
                if (id == null) return
                findNavController().navigate(ProfileFragmentDirections.actionToOrderDetailsFragment(id))
            }
        }
    }

    private fun observeTabReselect() {
        lifecycleScope.launchWhenStarted {
            tabManager.observeTabReselect()
                .collect {
                    if (it != TabManager.DEFAULT_STATE && it == R.id.profileFragment) {
                        binding.profileFlowRv.post {
                            binding.profileFlowRv.smoothScrollToPosition(0)
                        }
                        tabManager.setDefaultState()
                    }
                }
        }
    }

}
/*
@AndroidEntryPoint
class ProfileFragment : ViewStateBaseDialogFragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    private val gridProductsAdapter = GridProductsAdapter(
        onProductClick = {
            findNavController().navigate(ProfileFragmentDirections.actionToProductDetailFragment(it))
        },
        onChangeFavoriteStatus = { productId, status ->
            viewModel.changeFavoriteStatus(productId, status)
        },
        onChangeCartQuantity = { productId, quantity ->
            viewModel.changeCart(productId, quantity)
        },
        onNotAvailableMore = {},
        onNotifyWhenBeAvailable = { id, name, picture ->
            findNavController().navigate(ProfileFragmentDirections.actionToPreOrderBS(
                id, name, picture
            ))
        },
    )

    private val ordersSliderFragment: OrdersSliderFragment by lazy {
        OrdersSliderFragment.newInstance(OrdersSliderConfig(
            containTitleContainer = false
        ))
    }

    private val viewedProductsSliderFragment: ProductsSliderFragment by lazy {
        ProductsSliderFragment.newInstance(ProductsSliderConfig(
            containShowAllButton = false
        ), true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.isAlreadyLogin()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentProfileBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initButtons()
        initOrdersSliderFragment()
        initViewedProductsSlider()
        initBestForYouProductsRecycler()
        observeViewModel()
    }

    override fun update() {
        viewModel.updateData()
    }

    private fun initOrdersSliderFragment() {
        ordersSliderFragment.initCallbacks(
            iOnOrderClick = { orderId ->
                findNavController().navigate(ProfileFragmentDirections.actionToOrderDetailsFragment(orderId))
            },
            iOnShowAllOrdersClick = {}
        )

        childFragmentManager.beginTransaction().replace(
            R.id.ordersSliderFragment,
            ordersSliderFragment
        ).commit()
    }

    private fun initBestForYouProductsRecycler() {
        val space = resources.getDimension(R.dimen.space_16).toInt()
        binding.bestForYouProductsRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.bestForYouProductsRecycler.adapter = gridProductsAdapter
        binding.bestForYouProductsRecycler.addItemDecoration(
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
    }

    private fun initButtons() {
        with(binding) {
            tvLogout.setOnClickListener { viewModel.logout() }
            header.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToUserDataFragment())
            }
            btnLoginOrReg.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToLoginFragment())
            }
            tvAddresses.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToSavedAddressesDialogFragment())
            }
            tvOrdersHistory.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToAllOrdersFragment())
            }
            tvDiscountCard.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToDiscountCardFragment())
            }
            binding.tvShippingPrice.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToWebViewFragment(
                    ApiConfig.ABOUT_DELIVERY_URL,
                    "Стоимость доставки"
                ))
            }
            binding.tvRepair.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToServiceDetailFragment(
                    listOf("sanitar", "remont").toTypedArray(),
                    "remont"
                ))
            }
            binding.tvSanitar.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToServiceDetailFragment(
                    listOf("sanitar", "remont").toTypedArray(),
                    "sanitar"
                ))
            }
            binding.tvPastPurchases.setOnClickListener { findNavController().navigate(ProfileFragmentDirections.actionToPastPurchasesFragment()) }
            binding.tvQuestionnaires.setOnClickListener { findNavController().navigate(ProfileFragmentDirections.actionToQuestionnairesFragment()) }
            binding.tvAboutDelivery.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToWebViewFragment(
                    ApiConfig.ABOUT_DELIVERY_URL,
                    "Стоимость доставки"
                ))
            }
            binding.tvAboutPay.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToWebViewFragment(
                    ApiConfig.ABOUT_PAY_URL,
                    "Об оплате"
                ))
            }
            binding.tvAboutApp.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionToAboutAppDialogFragment())
            }
        }
    }

    private fun initViewedProductsSlider() {
        viewedProductsSliderFragment.initCallbacks(
            iOnProductClick = { productId -> findNavController().navigate(ProfileFragmentDirections.actionToProductDetailFragment(productId)) },
            iOnChangeProductQuantity = { pair -> viewModel.changeCart(pair.first, pair.second) },
            iOnFavoriteClick = { pair -> viewModel.changeFavoriteStatus(pair.first, pair.second) },
            iOnShowAllProductsClick = {},
            onNotAvailableMore = {},
            onNotifyWhenBeAvailable = { id, name, picture ->
                findNavController().navigate(ProductDetailsFragmentDirections.actionToPreOrderBS(id, name, picture))
            }
        )

        childFragmentManager.beginTransaction().replace(
            R.id.viewedProductsSliderFragment,
            viewedProductsSliderFragment
        ).commit()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        viewModel.isAlreadyLoginLD.observe(viewLifecycleOwner) { isAlreadyLogin ->
            when(isAlreadyLogin) {
                true -> {
                    //requireActivity().setTheme(R.style.Theme_Vodovoz_DarkStatusBar)
                    binding.profileContainer.visibility = View.VISIBLE
                    binding.noLoginContainer.visibility = View.GONE
                }
                false -> {
                    //requireActivity().setTheme(R.style.Theme_Vodovoz)
                    binding.profileContainer.visibility = View.GONE
                    binding.noLoginContainer.visibility = View.VISIBLE
                    onStateSuccess()
                }
            }
        }

        viewModel.userDataUILD.observe(viewLifecycleOwner) { userDataUI ->
            fillUserData(userDataUI)
        }

        viewModel.orderUIListLD.observe(viewLifecycleOwner) { orderUIList ->
            ordersSliderFragment.updateData(orderUIList)
        }

        viewModel.viewedProductsSliderLD.observe(viewLifecycleOwner) { categoryDetailUIList ->
            when(categoryDetailUIList.isEmpty()) {
                true -> binding.viewedProductsSliderFragment.visibility = View.GONE
                false -> viewedProductsSliderFragment.updateData(categoryDetailUIList)
            }
        }

        viewModel.bestForYouProductsListLD.observe(viewLifecycleOwner) { categoryDetailUI ->
            gridProductsAdapter.productUiList = categoryDetailUI.productUIList
            gridProductsAdapter.notifyDataSetChanged()

            binding.tvTitleBestForYou.text = categoryDetailUI.name
        }

        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Loading -> onStateLoading()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Hide -> onStateHide()
                is ViewState.Success -> onStateSuccess()
            }
        }
    }


    private fun fillUserData(userDataUI: UserDataUI) {
        Glide.with(requireContext())
            .load(userDataUI.avatar)
            .placeholder(ContextCompat.getDrawable(requireContext(), R.drawable.circleimageprofile))
            .into(binding.avatar)

        binding.name.text = StringBuilder()
            .append(userDataUI.firstName)
            .append(" ")
            .append(userDataUI.secondName)
            .toString()
    }

    override fun onResume() {
        super.onResume()
        viewModel.isAlreadyLogin()
    }

    override fun onStop() {
        super.onStop()
        //requireActivity().setTheme(R.style.Theme_Vodovoz)
    }

}*/
