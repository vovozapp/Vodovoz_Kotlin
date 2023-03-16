package com.vodovoz.app.feature.cart

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentMainCartFlowBinding
import com.vodovoz.app.feature.cart.adapter.CartMainClickListener
import com.vodovoz.app.feature.cart.viewholders.cartempty.CartEmpty
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
import com.vodovoz.app.ui.fragment.cart.CartFragmentDirections
import com.vodovoz.app.ui.fragment.replacement_product.ReplacementProductsSelectionBS
import com.vodovoz.app.ui.model.ProductUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFlowFragment : BaseFragment() {

    companion object {
        const val GIFT_ID = "GIFT_ID"
    }

    override fun layout(): Int = R.layout.fragment_main_cart_flow
    private val binding: FragmentMainCartFlowBinding by viewBinding {
        FragmentMainCartFlowBinding.bind(
            contentView
        )
    }
    private val viewModel: CartFlowViewModel by viewModels()

    private val cartController by lazy {
        CartController(
            getCartMainClickListener(),
            getProductsClickListener()
        )
    }

    private fun getCartMainClickListener(): CartMainClickListener {
        return object : CartMainClickListener {
            override fun onSwapProduct(id: Long) {
                /*viewModel.notAvailableProductUIList.find { it.id == productId }?.let {
                    findNavController().navigate(
                        CartFragmentDirections.actionToReplacementProductsSelectionBS(
                            it.detailPicture,
                            it.replacementProductUIList.toTypedArray(),
                            it.id,
                            it.name
                        ))
                }*/ //todo
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

            override fun onChangeProductQuantity(id: Long, cartQuantity: Int) {
                viewModel.changeCart(id, cartQuantity)
            }

            override fun onFavoriteClick(id: Long, isFavorite: Boolean) {
                viewModel.changeFavoriteStatus(id, isFavorite)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.firstLoad()
        observeUiState()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindButtons()
        initActionBar()
        observeResultLiveData()
        cartController.bind(binding.mainRv)
    }

    private fun initActionBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.incAppBar.tbToolbar)
        binding.incAppBar.tbToolbar.overflowIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_more_actions)
        binding.incAppBar.tvTitle.text = requireContext().getString(R.string.cart_title)
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect { cartState ->

                    if (cartState.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    val availableItems = cartState.data.availableProducts?.items ?: emptyList()

                    if (availableItems.isEmpty()) {
                        binding.bottom.root.isVisible = false
                        cartController.submitList(listOfNotNull(cartState.data.cartEmpty, cartState.data.bestForYouProducts))
                    } else {
                        binding.bottom.root.isVisible = true
                        cartController.submitList(listOfNotNull(cartState.data.notAvailableProducts, cartState.data.availableProducts, cartState.data.total))
                    }
                }
        }
    }

    private fun bindButtons() {
        binding.bottom.llShowGifts.setOnClickListener {
            /*when (viewModel.isLoginAlready()) {
                true -> {
                    findNavController().navigate(
                        CartFragmentDirections.actionToGiftsBottomFragment(
                            viewModel.getGiftList().toTypedArray()
                        ))
                }
                false -> findNavController().navigate(CartFragmentDirections.actionToProfileFragment())
            }*/ //todo
        }
        binding.bottom.btnRegOrder.setOnClickListener {
            /*findNavController().navigate(
                CartFragmentDirections.actionToOrderingFragment(
                    viewModel.total, viewModel.discount, viewModel.deposit, viewModel.full, viewModel.getCart(), viewModel.coupon
                ))*/ //todo
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
                gift.cartQuantity++
                viewModel.changeCart(gift.id, gift.cartQuantity)
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