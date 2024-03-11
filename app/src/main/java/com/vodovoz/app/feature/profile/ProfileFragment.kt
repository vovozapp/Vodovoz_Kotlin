package com.vodovoz.app.feature.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
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
import com.vodovoz.app.common.content.itemadapter.bottomitem.BottomProgressItem
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
import com.vodovoz.app.util.extensions.startTelegram
import com.vodovoz.app.util.extensions.startViber
import com.vodovoz.app.util.extensions.startWhatsUp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_profile_flow

    private val binding: FragmentProfileFlowBinding by viewBinding {
        FragmentProfileFlowBinding.bind(
            contentView
        )
    }

    internal val viewModel: ProfileFlowViewModel by activityViewModels()
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
    lateinit var accountManager: AccountManager

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
        ) {
            viewModel.repeatOrder(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeUiState()
        observeEvents()
        observeTabReselect()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileController.bind(binding.profileFlowRv, binding.refreshContainer)

        bindErrorRefresh { viewModel.refresh() }
        bindRegOrLoginBtn()
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
        lifecycleScope.launch {
            viewModel.observeEvent()
                .collect {
                    when (it) {
                        is ProfileFlowViewModel.ProfileEvents.Logout -> {
                            binding.profileFlowRv.isVisible = false
                            binding.noLoginContainer.isVisible = true

                            flowViewModel.refresh()
                            cartFlowViewModel.refreshIdle()
                            favoriteViewModel.refreshIdle()
                        }
                        is ProfileFlowViewModel.ProfileEvents.GoToCart -> {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Товары добавлены в корзину")
                                .setMessage("Перейти в корзину?")
                                .setPositiveButton("Да") { dialog, _ ->
                                    dialog.dismiss()
                                    tabManager.selectTab(R.id.graph_cart)
                                }
                                .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
                                .show()
                        }
                    }
                }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeUiState()
                    .collect { profileState ->

                        if (profileState.loadingPage) {
                            showLoaderWithBg(true)
                        } else {
                            showLoaderWithBg(false)
                        }

                        if (profileState.data.isLogin) {
                            binding.profileFlowRv.isVisible = true
                            binding.noLoginContainer.isVisible = false
                        } else {
                            binding.profileFlowRv.isVisible = false
                            binding.noLoginContainer.isVisible = true
                        }

                        val list = profileState.data.items
                        val progressList = if (!profileState.data.isSecondLoad) {
                            list + BottomProgressItem()
                        } else {
                            list
                        }
                        profileController.submitList(progressList)

                        showError(profileState.error)

                    }
            }
        }
    }

    private fun getProfileFlowClickListener(): ProfileFlowClickListener {
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

            override fun onUrlClick(url: String?) {
                if (url == null) return
                findNavController().navigate(
                    ProfileFragmentDirections.actionToWebViewFragment(
                        url,
                        "Стоимость доставки"
                    )
                )
            }

            override fun onUrlTwoClick(url: String?) {
                kotlin.runCatching {
                    if (url == null) return
                    val uri = Uri.parse(url)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
            }

            override fun onOrdersHistoryClick() {
                findNavController().navigate(ProfileFragmentDirections.actionToAllOrdersFragment())
            }

            override fun onLastPurchasesClick() {
                findNavController().navigate(ProfileFragmentDirections.actionToPastPurchasesFragment())
            }

            override fun onRepairClick() {
                findNavController().navigate(
                    ProfileFragmentDirections.actionToServiceDetailFragment(
                        listOf("sanitar", "remont").toTypedArray(),
                        "remont"
                    )
                )
            }

            override fun onOzonClick() {
                findNavController().navigate(
                    ProfileFragmentDirections.actionToServiceDetailFragment(
                        listOf("sanitar", "remont").toTypedArray(),
                        "sanitar"
                    )
                )
            }

            override fun onQuestionnaireClick() {
                findNavController().navigate(ProfileFragmentDirections.actionToQuestionnairesFragment())
            }

            override fun onNotificationsClick() {
                //   openNotificationSettingsForApp(requireContext())
                findNavController().navigate(R.id.notificationSettingsFragment)
            }

            override fun onAboutDeliveryClick() {
                findNavController().navigate(
                    ProfileFragmentDirections.actionToWebViewFragment(
                        ApiConfig.ABOUT_DELIVERY_URL,
                        "Стоимость доставки"
                    )
                )
            }

            override fun onAboutPaymentClick() {
                findNavController().navigate(
                    ProfileFragmentDirections.actionToWebViewFragment(
                        ApiConfig.ABOUT_PAY_URL,
                        "Об оплате"
                    )
                )
            }

            override fun onMyChatClick() {
                findNavController().navigate(
                    ProfileFragmentDirections.actionToWebViewFragment(
                        "http://jivo.chat/mk31km1IlP",
                        "Чат"
                    )
                )
            }

            override fun onSafetyClick() {
                /*kotlin.runCatching {
                    val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                    startActivity(intent)
                }*/
                findNavController().navigate(R.id.biometricSettingsFragment)
            }

            override fun onAboutAppClick() {
                findNavController().navigate(ProfileFragmentDirections.actionToAboutAppDialogFragment())
            }

            override fun onNewWaterApp() {
                val eventParameters = "\"source\":\"profile\""
                accountManager.reportYandexMetrica("trekervodi_zapysk", eventParameters)
                findNavController().navigate(R.id.waterAppFragment)
            }

            override fun onFetchDiscount() {
                findNavController().navigate(ProfileFragmentDirections.actionToDiscountCardFragment())
            }

            override fun onActiveCertificate() {
                findNavController().navigate(ProfileFragmentDirections.actionToActiveCertificateFragment())
            }

            override fun onBuyCertificate() {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToBuyCertificateFragment())
            }

            override fun onTelegramClick(phone: String?) {
                if (phone != null) {
                    requireActivity().startTelegram(phone)
                }
            }

            override fun onWhatsUpClick(phone: String?) {
                if (phone != null) {
                    requireActivity().startWhatsUp(phone)
                }
            }

            override fun onViberClick(phone: String?) {
                if (phone != null) {
                    requireActivity().startViber(phone)
                }
            }

        }
    }

    private fun getProductsShowClickListener(): ProductsShowAllListener {
        return object : ProductsShowAllListener {}
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                findNavController().navigate(
                    ProfileFragmentDirections.actionToProductDetailFragment(
                        id
                    )
                )
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                findNavController().navigate(
                    ProfileFragmentDirections.actionToPreOrderBS(
                        id, name, detailPicture
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

            }
        }
    }

    private fun getHomeOrdersSliderClickListener(): HomeOrdersSliderClickListener {
        return object : HomeOrdersSliderClickListener {
            override fun onOrderClick(id: Long?) {
                if (id == null) return
                findNavController().navigate(
                    ProfileFragmentDirections.actionToOrderDetailsFragment(
                        id
                    )
                )
            }
        }
    }

    private fun observeTabReselect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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

}