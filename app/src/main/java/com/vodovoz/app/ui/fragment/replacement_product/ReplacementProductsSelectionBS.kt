package com.vodovoz.app.ui.fragment.replacement_product

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.databinding.BsReplacementProductsBinding
import com.vodovoz.app.ui.adapter.LinearProductsAdapter
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.view.Divider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReplacementProductsSelectionBS : BottomSheetDialogFragment() {

    companion object {
        const val SELECTED_PRODUCT_ID = "SELECTED_PRODUCT_ID"
        const val CHANGE_CART = "CHANGE_CART"
    }

    private lateinit var binding: BsReplacementProductsBinding
    private val viewModel: ReplacementProductsSelectionViewModel by viewModels()

    private lateinit var productUIList: List<ProductUI>
    private lateinit var notAvailableProductDetailPicture: String

    private val linearProductsAdapter = LinearProductsAdapter(
        onProductClick = {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(SELECTED_PRODUCT_ID, it)
        },
        onChangeFavoriteStatus = { productId, status ->
            viewModel.changeFavoriteStatus(productId, status)
        },
        onChangeCartQuantity = { productId, quantity ->
            viewModel.changeCart(productId, quantity)
        },
        onNotAvailableMore = {},
        onNotifyWhenBeAvailable = { _, _, _ -> },
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    private fun getArgs() {
        ReplacementProductsSelectionBSArgs.fromBundle(requireArguments()).let { args ->
            productUIList = args.productList.toList()
            notAvailableProductDetailPicture = args.notAvailableProductDetailPicture
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = BsReplacementProductsBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHeader()
        setupProductsRecycler()
        observeViewModel()
        initDialog()
    }

    private fun setupHeader() {
        binding.imgClose.setOnClickListener { dismiss() }
        binding.imgAlert.setOnClickListener {
            findNavController().navigate(ReplacementProductsSelectionBSDirections.actionToPreOrderBS(
                ReplacementProductsSelectionBSArgs.fromBundle(requireArguments()).productId,
                ReplacementProductsSelectionBSArgs.fromBundle(requireArguments()).productName,
                ReplacementProductsSelectionBSArgs.fromBundle(requireArguments()).notAvailableProductDetailPicture
                ))
        }
        Glide.with(requireContext()).load(notAvailableProductDetailPicture).into(binding.imgProduct)
    }

    private fun setupProductsRecycler() {
        val space = resources.getDimension(R.dimen.space_16).toInt()
        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())
        ContextCompat.getDrawable(requireContext(), R.drawable.bkg_gray_divider)?.let {
            binding.rvProducts.addItemDecoration(Divider(it, space))
        }
        linearProductsAdapter.productUIList = productUIList
        binding.rvProducts.adapter = linearProductsAdapter
        val lastItemBottomSpace = resources.getDimension(R.dimen.last_item_bottom_normal_space).toInt()
        binding.rvProducts.addMarginDecoration {  rect, view, parent, state ->
            rect.top = space
            rect.right = space
            rect.bottom =
                if (parent.getChildAdapterPosition(view) == state.itemCount - 1) lastItemBottomSpace
                else space
        }


    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        findNavController().previousBackStackEntry?.savedStateHandle?.set(CHANGE_CART, viewModel.isChangeCart)
    }

    private fun initDialog() {
        dialog?.let {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
//                override fun onStateChanged(bottomSheet: View, newState: Int) {
//                    Toast.makeText(requireContext(), newState.toString(), Toast.LENGTH_LONG).show()
//                    if (BottomSheetBehavior.STATE_HIDDEN == newState) {
//                        findNavController().previousBackStackEntry?.savedStateHandle?.set(CHANGE_CART, viewModel.isChangeCart)
//                    }
//                }
//
//                override fun onSlide(bottomSheet: View, slideOffset: Float) {
//
//                }
//            })
        }
    }

    private fun observeViewModel() {
        viewModel.errorLD.observe(viewLifecycleOwner) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
        }
    }

}