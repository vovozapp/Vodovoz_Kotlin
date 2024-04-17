package com.vodovoz.app.feature.bottom.services.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.databinding.FragmentServiceDetailsFlowNewBinding
import com.vodovoz.app.feature.bottom.services.detail.adapter.ServiceDetailController
import com.vodovoz.app.feature.bottom.services.detail.model.ServiceDetailBlockUI
import com.vodovoz.app.feature.bottom.services.newservs.AboutServicesNewViewModel
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ServiceDetailNewFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_service_details_flow_new

    internal val viewModel: AboutServicesNewViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    internal val serviceDetailController by lazy { ServiceDetailController(cartManager, likeManager, ratingProductManager, getProductsClickListener()) }

    internal val binding: FragmentServiceDetailsFlowNewBinding by viewBinding {
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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeEvent()
                    .collect {

                    }
            }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeUiState()
                    .collect { state ->
                        if (state.loadingPage) {
                            showLoader()
                        } else {
                            //  hideLoader()
                        }

                        val description = state.data.detailItem?.description ?: ""
                        val title = state.data.detailItem?.name ?: ""

                        initWebView(
                            description,
                            state.data.detailItem?.preview,
                            state.data.detailItem?.blocksList
                        )

                        initToolbar(title)

                        showError(state.error)

                    }
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
            binding.serviceDetailWebView.loadDataWithBaseURL(null, url.prepareServiceHtml(), "text/html", "utf-8", null)
        } catch (e: Throwable) {
            showError(e.toErrorState())
        }
    }

    private fun String.prepareServiceHtml() : String {
        return "<style>img{display: inline;height: auto;max-width: 100%;}</style>$this"
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {

            override fun onProductClick(id: Long) {
                findNavController().navigate(ServiceDetailNewFragmentDirections.actionToProductDetailFragment(id))
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                when (viewModel.isLoginAlready()) {
                    true -> findNavController().navigate(
                        ServiceDetailNewFragmentDirections.actionToPreOrderBS(
                            id,
                            name,
                            detailPicture
                        )
                    )
                    false -> findNavController().navigate(ServiceDetailNewFragmentDirections.actionToProfileFragment())
                }
            }

            override fun onChangeProductQuantity(id: Long, cartQuantity: Int, oldQuantity: Int) {

            }

            override fun onFavoriteClick(id: Long, isFavorite: Boolean) {
                viewModel.changeFavoriteStatus(id, isFavorite)
            }

            override fun onChangeRating(id: Long, rating: Float, oldRating: Float) {
                viewModel.changeRating(id, rating, oldRating)
            }

            override fun onChangeProductQuantityServiceDetails(
                id: Long,
                cartQuantity: Int,
                oldQuantity: Int,
                giftId: String
            ) {
                viewModel.changeCart(id, cartQuantity, giftId)
            }
        }
    }

}