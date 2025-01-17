package com.vodovoz.app.feature.all.orders.detail

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.databinding.FragmentOrderDetailsFlowBinding
import com.vodovoz.app.feature.all.orders.detail.prices.OrderPricesAdapter
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setDoublePriceText
import com.vodovoz.app.ui.extensions.ViewExtensions.openLink
import com.vodovoz.app.ui.model.OrderDetailsUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OrderDetailsFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_order_details_flow

    private val binding: FragmentOrderDetailsFlowBinding by viewBinding {
        FragmentOrderDetailsFlowBinding.bind(
            contentView
        )
    }
    internal val viewModel: OrderDetailsFlowViewModel by viewModels()

//    private val args: OrderDetailsFragmentArgs by navArgs()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var accountManager: AccountManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    private val orderDetailsController by lazy {
        OrderDetailsController(
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

        orderDetailsController.bind(binding.rvProducts)
        bindErrorRefresh { viewModel.refreshSorted() }
        initButtons()
        observeUiState()
        observeCancelResult()
        initActionBar()
    }

    private fun initActionBar() {
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeCancelResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel
                    .observeCancelResult()
                    .collect { message ->
                        binding.llStatusContainer.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.color_status_canceled
                            )
                        )
                        binding.llActionsContainer.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.color_status_canceled
                            )
                        )
                        binding.imgStatus.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_order_canceled
                            )
                        )
                        binding.tvStatus.text = getString(R.string.canceled)
                        binding.tvCancelOrder.visibility = View.INVISIBLE
                        binding.llPayOrder.visibility = View.GONE
                        MaterialAlertDialogBuilder(requireContext())
                            .setMessage(message)
                            .setPositiveButton("Ок") { dialog, _ -> dialog.dismiss() }
                            .show()
                    }
            }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeUiState()
                    .collect { state ->

                        showLoaderWithBg(state.loadingPage)

                        binding.llPayOrder.setOnClickListener {
                            val url = state.data.orderDetailsUI?.payUri ?: return@setOnClickListener
                            binding.root.openLink(url)
                        }

                        val data = state.data
                        fillOrderData(data)

                        val products = data.orderDetailsUI?.productUIList
                        if (products != null) {
                            orderDetailsController.submitList(products)
                        }

                        if (data.ifRepeatOrder) {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Товары добавлены в корзину")
                                .setMessage("Перейти в корзину?")
                                .setPositiveButton("Да") { dialog, _ ->
                                    dialog.dismiss()
                                    if (findNavController().currentBackStackEntry?.destination?.id == R.id.orderDetailsFragment) {
                                        findNavController().navigate(OrderDetailsFragmentDirections.actionToCartFragment())
                                    }
                                }
                                .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
                                .show()
                            viewModel.repeatOrderFlagReset()
                        }

                        showError(state.error)

                    }
            }
        }
    }

    private fun fillOrderData(data: OrderDetailsFlowViewModel.OrderDetailsState) {
        val orderDetailsUI: OrderDetailsUI = data.orderDetailsUI ?: return
        with(binding) {
            binding.tvTitle.text = StringBuilder()
                .append("Заказ от ")
                .append(orderDetailsUI.dateOrder)
                .toString()
            binding.tvSubtitle.text = StringBuilder()
                .append("№")
                .append(orderDetailsUI.id)
                .toString()
            when (orderDetailsUI.status?.id) {
                "F",
                "S",
                "R",
                -> binding.tvCancelOrder.visibility = View.INVISIBLE

                else -> binding.tvCancelOrder.visibility = View.VISIBLE
            }

            when (orderDetailsUI.isPayed) {
                true -> {
                    binding.tvPayStatus.text = getString(R.string.payed)
                    binding.payedStatus.isVisible = true
                    statusSpacer.isVisible = true
                    binding.llPayOrder.visibility = View.GONE
                }

                false -> {
                    when (orderDetailsUI.payUri.isEmpty()) {
                        true -> binding.llPayOrder.visibility = View.GONE
                        false -> binding.llPayOrder.visibility = View.VISIBLE
                    }
                    binding.tvPayStatus.text = getString(R.string.not_payed)
                    binding.payedStatus.isVisible = false
                    statusSpacer.isVisible = false
                }
            }

            when (orderDetailsUI.status?.id) {
                "E" -> {
                    if (data.ifDriverExists && orderDetailsUI.driverName != null) {
                        binding.btnTraceOrder.isVisible = true
                        binding.btnTraceOrder.text = orderDetailsUI.driverName

                        binding.btnTraceOrder.setOnClickListener {
                            if (orderDetailsUI.driverId == null || orderDetailsUI.id == null) return@setOnClickListener
                            val eventParameters = "\"ZakazNumber\":\"${orderDetailsUI.id}\""
                            accountManager.reportEvent("Где мой заказ", eventParameters)

                            viewModel.postUrl(orderDetailsUI.driverUrl)

                            debugLog { "driverId ${orderDetailsUI.driverId}" }

                            findNavController().navigate(
                                OrderDetailsFragmentDirections.actionToTraceOrderFragment(
                                    orderDetailsUI.driverId,
                                    orderDetailsUI.driverName,
                                    orderDetailsUI.id.toString()
                                )
                            )
                        }

                    } else {
                        binding.btnTraceOrder.isVisible = false
                    }
                }

                "R" -> {
                    binding.payedStatus.isVisible = false
                    statusSpacer.isVisible = false
                    binding.llPayOrder.visibility = View.GONE
                }

                "D" -> {
                    binding.llPayOrder.visibility = View.GONE
                }

            } //todo

            llStatusContainer.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    orderDetailsUI.status!!.color
                )
            )
            llActionsContainer.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    orderDetailsUI.status.color
                )
            )
            tvTotalPriceHeader.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    orderDetailsUI.status.color
                )
            )
            imgStatus.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    orderDetailsUI.status.image
                )
            )
            tvTotalPriceHeader.setDoublePriceText(orderDetailsUI.totalPrice)
            tvShippingDate.text = orderDetailsUI.dateDelivery
            tvShippingInterval.text = orderDetailsUI.deliveryTimeInterval
            tvPayMethod.text = orderDetailsUI.payMethod
            tvStatus.text = orderDetailsUI.status.statusName
            tvStatus.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    orderDetailsUI.status.color
                )
            )
            if (orderDetailsUI.address.isNotEmpty()) {
                tvAddress.text = orderDetailsUI.address
            } else {
                tvAddress.visibility = View.GONE
                mdDividerAfterAddress.visibility = View.GONE
                tvAddressTitle.visibility = View.GONE
            }
            tvConsumerName.text = StringBuilder()
                .append(orderDetailsUI.userFirstName)
                .append(" ")
                .append(orderDetailsUI.userSecondName)
            tvConsumerPhone.text = orderDetailsUI.userPhone

            rvPrices.adapter = OrderPricesAdapter().apply {
                submitList(orderDetailsUI.orderPricesUIList)
            }

            if (!orderDetailsUI.repeatOrder) {
                binding.llRepeatOrder.visibility = View.GONE
            }

            val any = orderDetailsUI.productUIList.any { it.isAvailable && it.canBuy }
            if (!any) {
                binding.llRepeatOrder.visibility = View.GONE
                if (binding.llPayOrder.visibility == View.VISIBLE) {
                    binding.tvNoProductsAvailable.visibility = View.GONE
                } else {
                    binding.tvNoProductsAvailable.visibility = View.VISIBLE
                }
            } else {
                binding.llRepeatOrder.visibility = View.VISIBLE
            }
//            tvProductsPrice.setPriceText(orderDetailsUI.productsPrice)
//            tvDepositPrice.setPriceText(orderDetailsUI.depositPrice)
//            tvShippingPrice.setPriceText(orderDetailsUI.deliveryPrice)
//            tvTotalPrice.setPriceText(orderDetailsUI.totalPrice)
        }
    }

    private fun initButtons() {
        binding.llRepeatOrder.setOnClickListener {
            viewModel.repeatOrder()
        }

        binding.tvCancelOrder.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("Вы уверены, что хотите отменить заказ?")
                .setPositiveButton("Да") { dialog, _ ->
                    viewModel.cancelOrder()
                    dialog.dismiss()
                }
                .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                findNavController().navigate(
                    OrderDetailsFragmentDirections.actionToProductDetailFragment(
                        id
                    )
                )
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                findNavController().navigate(
                    OrderDetailsFragmentDirections.actionToPreOrderBS(
                        id, name, detailPicture
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

            }
        }
    }
}
