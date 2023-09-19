package com.vodovoz.app.feature.replacement

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.databinding.BsReplacementProductsBinding
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReplacementProductsSelectionBS : BaseBottomSheetFragment() {

    companion object {
        const val SELECTED_PRODUCT_ID = "SELECTED_PRODUCT_ID"
        const val CHANGE_CART = "CHANGE_CART"
    }

    override fun layout(): Int {
        return R.layout.bs_replacement_products
    }

    private val binding: BsReplacementProductsBinding by viewBinding { BsReplacementProductsBinding.bind(contentView) }

    internal val viewModel: ReplacementFlowViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    private val args: ReplacementProductsSelectionBSArgs by navArgs()

    private val replacementController by lazy {
        ReplacementController(cartManager, likeManager, getProductsClickListener(), requireContext(), ratingProductManager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()

        replacementController.bind(binding.rvProducts, args.productList.toList())

        initDialog()
    }

    private fun setupHeader() {
        binding.imgClose.setOnClickListener {
            dismiss()
        }
        binding.imgAlert.setOnClickListener {
            findNavController().navigate(ReplacementProductsSelectionBSDirections.actionToPreOrderBS(
                args.productId,
                args.productName,
                args.notAvailableProductDetailPicture
            ))
        }
        Glide.with(requireContext())
            .load(args.notAvailableProductDetailPicture)
            .into(binding.imgProduct)

        binding.btnAllProducts.setOnClickListener {
            val id = args.categoryId
            if (id != -1L) {
                findNavController().navigate(
                    ReplacementProductsSelectionBSDirections.actionToPaginatedProductsCatalogFragment(
                        id
                    )
                )
            }
        }
    }

    private fun initDialog() {
        dialog?.let {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    SELECTED_PRODUCT_ID, id)
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {

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

}
