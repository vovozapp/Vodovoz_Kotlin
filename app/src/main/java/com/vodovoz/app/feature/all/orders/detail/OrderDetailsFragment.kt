package com.vodovoz.app.feature.all.orders.detail

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.databinding.FragmentOrderDetailsFlowBinding
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.extensions.ViewExtensions.openLink
import com.vodovoz.app.ui.model.OrderDetailsUI
import com.vodovoz.app.ui.model.OrderStatusUI
import com.vodovoz.app.util.extensions.debugLog
import com.yandex.metrica.YandexMetrica
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderDetailsFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_order_details_flow

    private val binding: FragmentOrderDetailsFlowBinding by viewBinding {
        FragmentOrderDetailsFlowBinding.bind(
            contentView
        )
    }
    private val viewModel: OrderDetailsFlowViewModel by viewModels()

    private val args: OrderDetailsFragmentArgs by navArgs()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var accountManager: AccountManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    private val orderDetailsController by lazy {
        OrderDetailsController(cartManager, likeManager, getProductsClickListener(), requireContext(), ratingProductManager)
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
        observeGoToCart()
        observeCancelResult()
        initActionBar()
    }

    private fun initActionBar() {
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeCancelResult() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeCancelResult()
                .collect {message ->
                    binding.llStatusContainer.setBackgroundColor(ContextCompat.getColor(requireContext(), OrderStatusUI.CANCELED.color))
                    binding.llActionsContainer.setBackgroundColor(ContextCompat.getColor(requireContext(), OrderStatusUI.CANCELED.color))
                    binding.imgStatus.setImageDrawable(ContextCompat.getDrawable(requireContext(), OrderStatusUI.CANCELED.image))
                    binding.tvStatus.text = "Отменен"
                    binding.tvCancelOrder.visibility = View.INVISIBLE
                    binding.llPayOrder.visibility = View.GONE
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(message)
                        .setPositiveButton("Ок") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
        }
    }

    private fun observeGoToCart() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeGoToCart()
                .collect {
                    if (it) {
                        if (findNavController().currentBackStackEntry?.destination?.id == R.id.orderDetailsFragment) {
                            findNavController().navigate(OrderDetailsFragmentDirections.actionToCartFragment())
                        }
                    }
                }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
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

                    showError(state.error)

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
            when(orderDetailsUI.status) {
                OrderStatusUI.COMPLETED,
                OrderStatusUI.DELIVERED,
                OrderStatusUI.CANCELED,
                -> binding.tvCancelOrder.visibility = View.INVISIBLE
                else -> binding.tvCancelOrder.visibility = View.VISIBLE
            }

            when(orderDetailsUI.isPayed) {
                true -> {
                    binding.tvPayStatus.text = "Оплачен"
                    binding.payedStatus.isVisible = true
                    binding.llPayOrder.visibility = View.GONE
                    binding.payedStatus.isVisible = false
                }
                false -> {
                    when(orderDetailsUI.payUri.isEmpty()) {
                        true -> binding.llPayOrder.visibility = View.GONE
                        false -> binding.llPayOrder.visibility = View.VISIBLE
                    }
                    binding.tvPayStatus.text = "Не оплачен"
                    binding.payedStatus.isVisible = false
                }
            }

            when(orderDetailsUI.status?.id) {
                "E" -> {
                    if (data.ifDriverExists && orderDetailsUI.driverName != null) {
                        binding.btnTraceOrder.isVisible = true
                        binding.btnTraceOrder.text = orderDetailsUI.driverName

                        binding.btnTraceOrder.setOnClickListener {
                            if (orderDetailsUI.driverId == null || orderDetailsUI.id == null) return@setOnClickListener
                            val eventParameters = "{\"ZakazNumber\":\"${orderDetailsUI.id}\"}"
                            //accountManager.reportYandexMetrica("Где мой заказ", eventParameters) //todo релиз

                            debugLog { "driverId ${orderDetailsUI.driverId}" }

                            findNavController().navigate(OrderDetailsFragmentDirections.actionToTraceOrderFragment(
                                orderDetailsUI.driverId, orderDetailsUI.driverName, orderDetailsUI.id.toString()
                            ))
                        }

                    } else {
                        binding.btnTraceOrder.isVisible = false
                    }

                }
                "R" -> binding.payedStatus.isVisible = false
            } //todo

            llStatusContainer.setBackgroundColor(ContextCompat.getColor(requireContext(), orderDetailsUI.status!!.color))
            llActionsContainer.setBackgroundColor(ContextCompat.getColor(requireContext(), orderDetailsUI.status.color))
            tvTotalPriceHeader.setBackgroundColor(ContextCompat.getColor(requireContext(), orderDetailsUI.status.color))
            imgStatus.setImageDrawable(ContextCompat.getDrawable(requireContext(), orderDetailsUI.status.image))
            tvTotalPriceHeader.setPriceText(orderDetailsUI.totalPrice)
            tvShippingDate.text = orderDetailsUI.dateDelivery
            tvShippingInterval.text = orderDetailsUI.deliveryTimeInterval
            tvPayMethod.text = orderDetailsUI.payMethod
            tvStatus.text = orderDetailsUI.status.statusName
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), orderDetailsUI.status.color))
            tvAddress.text = orderDetailsUI.address
            tvConsumerName.text = StringBuilder()
                .append(orderDetailsUI.userFirstName)
                .append(" ")
                .append(orderDetailsUI.userSecondName)
            tvConsumerPhone.text = orderDetailsUI.userPhone
            tvProductsPrice.setPriceText(orderDetailsUI.productsPrice)
            tvDepositPrice.setPriceText(orderDetailsUI.depositPrice)
            tvShippingPrice.setPriceText(orderDetailsUI.deliveryPrice)
            tvTotalPrice.setPriceText(orderDetailsUI.totalPrice)
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
                findNavController().navigate(OrderDetailsFragmentDirections.actionToProductDetailFragment(id))
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                findNavController().navigate(OrderDetailsFragmentDirections.actionToPreOrderBS(
                    id, name, detailPicture
                ))
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
