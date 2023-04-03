package com.vodovoz.app.feature.replacement

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.BsReplacementProductsBinding
import com.vodovoz.app.databinding.FragmentProductDetailsFlowBinding
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.inner.AvailableProductsAdapter
import com.vodovoz.app.feature.catalog.CatalogFragmentDirections
import com.vodovoz.app.feature.productdetail.ProductDetailsFragmentDirections
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.adapter.LinearProductsAdapter
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.fragment.replacement_product.ReplacementProductsSelectionBSArgs
import com.vodovoz.app.ui.fragment.replacement_product.ReplacementProductsSelectionBSDirections
import com.vodovoz.app.ui.fragment.replacement_product.ReplacementProductsSelectionViewModel
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.view.Divider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReplacementFlowFragment : BaseBottomSheetFragment() {

    companion object {
        const val SELECTED_PRODUCT_ID = "SELECTED_PRODUCT_ID"
        const val CHANGE_CART = "CHANGE_CART"
    }

    private val binding: BsReplacementProductsBinding by viewBinding { BsReplacementProductsBinding.bind(contentView) }

    private val viewModel: ReplacementFlowViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    private val productsAdapter by lazy {
        AvailableProductsAdapter(getProductsClickListener(), likeManager, cartManager)
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(SELECTED_PRODUCT_ID, id)
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {

            }

            override fun onChangeProductQuantity(id: Long, cartQuantity: Int, oldQuantity: Int) {
                viewModel.changeCart(id, cartQuantity, oldQuantity)
            }

            override fun onFavoriteClick(id: Long, isFavorite: Boolean) {
                viewModel.changeFavoriteStatus(id, isFavorite)
            }

        }
    }

    override fun layout(): Int {
        return R.layout.bs_replacement_products
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupProductsRecycler()

        val products = ReplacementProductsSelectionBSArgs.fromBundle(requireArguments()).productList.toList()
        productsAdapter.submitList(products)

        initDialog()
    }

    private fun setupHeader() {

        val pic = ReplacementProductsSelectionBSArgs.fromBundle(requireArguments()).notAvailableProductDetailPicture

        binding.imgClose.setOnClickListener { dismiss() }
        binding.imgAlert.setOnClickListener {
            findNavController().navigate(ReplacementProductsSelectionBSDirections.actionToPreOrderBS(
                ReplacementProductsSelectionBSArgs.fromBundle(requireArguments()).productId,
                ReplacementProductsSelectionBSArgs.fromBundle(requireArguments()).productName,
                pic
            ))
        }
        Glide.with(requireContext()).load(pic).into(binding.imgProduct)

        binding.btnAllProducts.setOnClickListener {
            //findNavController().navigate(CatalogFragmentDirections.actionToPaginatedProductsCatalogFragment())
        }
    }

    private fun setupProductsRecycler() {
        val space = resources.getDimension(R.dimen.space_16).toInt()

        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())

        ContextCompat.getDrawable(requireContext(), R.drawable.bkg_gray_divider)?.let {
            binding.rvProducts.addItemDecoration(Divider(it, space))
        }

        binding.rvProducts.adapter = productsAdapter

        val lastItemBottomSpace = resources.getDimension(R.dimen.last_item_bottom_normal_space).toInt()

        binding.rvProducts.addMarginDecoration {  rect, view, parent, state ->
            rect.top = space
            rect.right = space
            rect.bottom =
                if (parent.getChildAdapterPosition(view) == state.itemCount - 1) lastItemBottomSpace
                else space
        }
    }

    private fun initDialog() {
        dialog?.let {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

}