package com.vodovoz.app.feature.onlyproducts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.common.speechrecognizer.SpeechDialogFragment
import com.vodovoz.app.databinding.FragmentFixAmountProductsBinding
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@AndroidEntryPoint
class ProductsCatalogFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_fix_amount_products

    private val binding: FragmentFixAmountProductsBinding by viewBinding {
        FragmentFixAmountProductsBinding.bind(
            contentView
        )
    }

    internal val viewModel: OnlyProductsViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    private val onlyProductsController by lazy {
        OnlyProductsController(
            viewModel,
            cartManager,
            likeManager,
            getProductsClickListener(),
            requireContext(),
            ratingProductManager
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.firstLoadSorted()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onlyProductsController.bind(binding.productRecycler, binding.refreshContainer)
        bindErrorRefresh { viewModel.refreshSorted() }
        observeUiState()
        initBackButton()
        initSearch()
    }

    private fun initSearch() {
        initSearchToolbar(
            { findNavController().navigate(ProductsCatalogFragmentDirections.actionToSearchFragment()) },
            { findNavController().navigate(ProductsCatalogFragmentDirections.actionToSearchFragment()) },
            { navigateToQrCodeFragment() },
            { startSpeechRecognizer() },
            true
        )
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

                        if (state.loadingPage) {
                            showLoader()
                        } else {
                            hideLoader()
                        }

                        val data = state.data
                        if (state.bottomItem != null) {
                            onlyProductsController.submitList(data.itemsList + state.bottomItem)
                        } else {
                            onlyProductsController.submitList(data.itemsList)
                        }

                        if (state.error !is ErrorState.Empty) {
                            showError(state.error)
                        }

                    }
            }
        }
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                findNavController().navigate(
                    ProductsCatalogFragmentDirections.actionToProductDetailFragment(
                        id
                    )
                )
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                findNavController().navigate(
                    ProductsCatalogFragmentDirections.actionToPreOrderBS(
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

    private val permissionsController by lazy {
        PermissionsController(requireContext())
    }

    private fun navigateToQrCodeFragment() {
        permissionsController.methodRequiresCameraPermission(requireActivity()) {
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
        permissionsController.methodRequiresRecordAudioPermission(requireActivity()) {
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

    sealed class DataSource : Parcelable {
        @Parcelize
        class BannerProducts(val categoryId: Long) : DataSource()
    }

}
