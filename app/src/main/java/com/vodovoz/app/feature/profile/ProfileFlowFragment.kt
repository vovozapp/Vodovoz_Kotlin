package com.vodovoz.app.feature.profile

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.databinding.FragmentProfileFlowBinding
import com.vodovoz.app.feature.home.HomeFlowViewModel
import com.vodovoz.app.feature.home.viewholders.homeorders.inneradapter.HomeOrdersSliderClickListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.profile.adapter.ProfileFlowClickListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFlowFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_profile_flow

    private val binding: FragmentProfileFlowBinding by viewBinding {
        FragmentProfileFlowBinding.bind(
            contentView
        )
    }

    private val viewModel: ProfileFlowViewModel by activityViewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var tabManager: TabManager

    private val profileController by lazy {
        ProfileFlowController(
            viewModel = viewModel,
            cartManager = cartManager,
            likeManager = likeManager,
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
    }

    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeEvent()
                .collect {

                }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect { profileState ->

                    if (profileState.data.isLogin) {
                        binding.profileFlowRv.isVisible = true
                        binding.noLoginContainer.isVisible = false
                    } else {
                        binding.profileFlowRv.isVisible = true
                        binding.noLoginContainer.isVisible = false
                    }

                    if (profileState.data.items.mapNotNull { it.item }.size < 4) {
                        binding.profileFlowRv.isVisible = false
                        showLoaderWithBg(true)
                    } else {
                        binding.profileFlowRv.isVisible = true
                        showLoaderWithBg(false)
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

            }

            override fun logout() {

            }

        }
    }

    private fun getProductsShowClickListener(): ProductsShowAllListener {
        return object : ProductsShowAllListener {}
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {

            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {

            }

            override fun onChangeProductQuantity(id: Long, cartQuantity: Int, oldQuantity: Int) {

            }

            override fun onFavoriteClick(id: Long, isFavorite: Boolean) {

            }

            override fun onChangeRating(id: Long, rating: Float, oldRating: Float) {

            }
        }
    }

    private fun getHomeOrdersSliderClickListener() : HomeOrdersSliderClickListener {
        return object : HomeOrdersSliderClickListener {
            override fun onOrderClick(id: Long?) {

            }
        }
    }

}