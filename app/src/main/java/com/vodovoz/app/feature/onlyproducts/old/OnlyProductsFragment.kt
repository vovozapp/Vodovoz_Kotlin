package com.vodovoz.app.feature.onlyproducts.old

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.databinding.FragmentFixAmountProductsBinding
import com.vodovoz.app.feature.onlyproducts.OnlyProductsController
import com.vodovoz.app.feature.onlyproducts.OnlyProductsViewModel
import com.vodovoz.app.feature.onlyproducts.ProductsCatalogFragmentDirections
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.util.extensions.snack
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class OnlyProductsFragment : BaseFragment() {


    override fun layout(): Int = R.layout.fragment_fix_amount_products

    private val binding: FragmentFixAmountProductsBinding by viewBinding {
        FragmentFixAmountProductsBinding.bind(
            contentView
        )
    }

    private val viewModel: OnlyProductsViewModel by viewModels()

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

        onlyProductsController.bind(binding.productRecycler, null)

        observeUiState()
        initBackButton()
        initSearch()
    }

    private fun initSearch() {
        initSearchToolbar(
            { findNavController().navigate(ProductsCatalogFragmentDirections.actionToSearchFragment()) },
            { findNavController().navigate(ProductsCatalogFragmentDirections.actionToSearchFragment()) },
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
        lifecycleScope.launchWhenStarted {
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

    sealed class DataSource : Serializable {
        class BannerProducts(val categoryId: Long) : DataSource()
    }

}