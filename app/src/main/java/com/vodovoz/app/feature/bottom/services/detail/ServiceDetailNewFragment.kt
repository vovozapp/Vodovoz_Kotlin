package com.vodovoz.app.feature.bottom.services.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.FragmentServiceDetailsFlowBinding
import com.vodovoz.app.databinding.FragmentServiceDetailsFlowNewBinding
import com.vodovoz.app.feature.bottom.services.AboutServicesFlowViewModel
import com.vodovoz.app.feature.bottom.services.detail.adapter.ServiceDetailClickListener
import com.vodovoz.app.feature.bottom.services.detail.adapter.ServiceDetailController
import com.vodovoz.app.feature.bottom.services.detail.model.ServiceDetailBlockUI
import com.vodovoz.app.feature.bottom.services.newservs.AboutServicesNewViewModel
import com.vodovoz.app.feature.favorite.FavoriteFragmentDirections
import com.vodovoz.app.feature.favorite.bestforyouadapter.BestForYouController
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class ServiceDetailNewFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_service_details_flow_new

    private val viewModel: AboutServicesNewViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    private val serviceDetailController by lazy { ServiceDetailController(cartManager, likeManager, getProductsClickListener()) }

    private val binding: FragmentServiceDetailsFlowNewBinding by viewBinding {
        FragmentServiceDetailsFlowNewBinding.bind(
            contentView
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchServiceDetailsData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        serviceDetailController.bind(binding.rvServices)
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
                .collect { state ->
                    if (state.loadingPage) {
                        showLoader()
                    } else {
                      //  hideLoader()
                    }

                    val description = state.data.detailItem?.description ?: ""
                    val title = state.data.detailItem?.name ?: ""

                    initWebView(description, state.data.detailItem?.preview, state.data.detailItem?.blocksList)

                    initToolbar(title)

                    showError(state.error)

                }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(url: String, preview: String?, blockList: List<ServiceDetailBlockUI>?) {
        binding.serviceDetailWebView.settings.javaScriptEnabled = true
        binding.serviceDetailWebView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                if (view.progress == 100) {
                    hideLoader()

                    Glide.with(requireContext())
                        .load(preview)
                        .into(binding.avatar)

                    serviceDetailController.submitList(blockList ?: emptyList())

                }
            }
        }

        try {
            binding.serviceDetailWebView.loadDataWithBaseURL(null, url, "text/html", "utf-8", null)
        } catch (e: Throwable) {
            showError(e.toErrorState())
        }
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {

            /*override fun onProductClick(id: Long) {
                findNavController().navigate(FavoriteFragmentDirections.actionToProductDetailFragment(id))
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                when (viewModel.isLoginAlready()) {
                    true -> findNavController().navigate(
                        FavoriteFragmentDirections.actionToPreOrderBS(
                            id,
                            name,
                            detailPicture
                        )
                    )
                    false -> findNavController().navigate(FavoriteFragmentDirections.actionToProfileFragment())
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
            }*/
            override fun onProductClick(id: Long) {
                TODO("Not yet implemented")
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                TODO("Not yet implemented")
            }

            override fun onChangeProductQuantity(id: Long, cartQuantity: Int, oldQuantity: Int) {
                TODO("Not yet implemented")
            }

            override fun onFavoriteClick(id: Long, isFavorite: Boolean) {
                TODO("Not yet implemented")
            }

            override fun onChangeRating(id: Long, rating: Float, oldRating: Float) {
                TODO("Not yet implemented")
            }

        }
    }

}