package com.vodovoz.app.feature.replacement

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.BsReplacementProductsBinding
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.inner.AvailableProductsAdapter
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration
import com.vodovoz.app.ui.view.Divider
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

    private val viewModel: ReplacementFlowViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    private val args: ReplacementProductsSelectionBSArgs by navArgs()

    private val replacementController by lazy {
        ReplacementController(cartManager, likeManager, getProductsClickListener(), requireContext())
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

            }
        }
    }

}
/*
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

}*/
