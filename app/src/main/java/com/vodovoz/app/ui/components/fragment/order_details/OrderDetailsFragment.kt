package com.vodovoz.app.ui.components.fragment.order_details

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentOrderDetailsBinding
import com.vodovoz.app.ui.components.adapter.LinearProductsAdapter
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.ViewStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.diffUtils.ProductDiffUtilCallback
import com.vodovoz.app.ui.components.fragment.orders_history.OrdersHistoryFragmentDirections
import com.vodovoz.app.ui.components.fragment.orders_history.OrdersHistoryViewModel
import com.vodovoz.app.ui.extensions.PriceTextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import com.vodovoz.app.ui.model.OrderDetailsUI
import com.vodovoz.app.ui.model.OrderStatusUI
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class OrderDetailsFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentOrderDetailsBinding
    private lateinit var viewModel: OrderDetailsViewModel

    private val compositeDisposable = CompositeDisposable()
    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>> = PublishSubject.create()
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>> = PublishSubject.create()

    private val linearProductsAdapter = LinearProductsAdapter(
        onProductClickSubject = onProductClickSubject,
        onChangeProductQuantitySubject = onChangeProductQuantitySubject,
        onFavoriteClickSubject = onFavoriteClickSubject
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        getArgs()
        subscribeSubjects()
    }

    private fun getArgs() {
        viewModel.updateArgs(OrderDetailsFragmentArgs.fromBundle(requireArguments()).orderId)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[OrderDetailsViewModel::class.java]
    }

    private fun subscribeSubjects() {
        onProductClickSubject.subscribeBy { productId ->
            findNavController().navigate(OrderDetailsFragmentDirections.actionToProductDetailFragment(productId))
        }.addTo(compositeDisposable)
    }

    override fun update() {}

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentOrderDetailsBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initActionBar()
        initProductsRecycler()
        initButtons()
        observeViewModel()
    }

    private fun initActionBar() {
        (requireActivity() as AppCompatActivity).let { appCompatActivity ->
            appCompatActivity.setSupportActionBar(binding.toolbar)
            appCompatActivity.supportActionBar?.setDisplayShowHomeEnabled(true)
            appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.scrollContainer.setScrollElevation(binding.appBar)
    }

    private fun initProductsRecycler() {
        val space = resources.getDimension(R.dimen.primary_space).toInt()
        binding.productsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.productsRecycler.adapter = linearProductsAdapter
        binding.productsRecycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.productsRecycler.addItemDecoration(
            object : RecyclerView.ItemDecoration() {override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                with(outRect) {
                    top = space
                    bottom = space
                    right = space
                }
            }}
        )
    }

    private fun initButtons() {
        binding.repeatOrder.setOnClickListener {
            viewModel.repeatOrder()
        }
        binding.payOrder.setOnClickListener {
            val openLinkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.orderDetailsUI.payUri))
            requireActivity().startActivity(openLinkIntent)
        }
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Hide -> onStateError("Неизвестная ошибка")
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Loading -> onStateLoading()
                is ViewState.Success -> onStateSuccess()
            }
        }

        viewModel.orderDetailsUILD.observe(viewLifecycleOwner) { orderDetailsUI ->
           fillOrderData(orderDetailsUI)
            updateProductsRecycler(orderDetailsUI.productUIList)
        }

        viewModel.errorLD.observe(viewLifecycleOwner) { errorMessage ->
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
        }

        viewModel.showCartLD.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                findNavController().navigate(OrderDetailsFragmentDirections.actionToCartFragment())
            }
        }
    }

    private fun fillOrderData(orderDetailsUI: OrderDetailsUI) {
        with(binding) {
            toolbar.title = StringBuilder()
                .append("Заказ от ")
                .append(orderDetailsUI.dateOrder)
                .toString()
            toolbar.subtitle = StringBuilder()
                .append("№")
                .append(orderDetailsUI.id)
                .toString()
            when(orderDetailsUI.status) {
                OrderStatusUI.COMPLETED,
                OrderStatusUI.CANCELED -> binding.cancelOrder.visibility = View.INVISIBLE
                else -> binding.cancelOrder.visibility = View.VISIBLE
            }
            when(orderDetailsUI.isPayed) {
                true -> binding.payStatusLabel.text = "Оплачен"
                false -> binding.payStatusLabel.text = "Не оплачен"
            }
            when(orderDetailsUI.payUri.isEmpty()) {
                false ->binding.payOrder.visibility = View.VISIBLE
                true -> binding.payOrder.visibility = View.GONE
            }

            statusContainer.setBackgroundColor(ContextCompat.getColor(requireContext(), orderDetailsUI.status!!.color))
            actionsContainer.setBackgroundColor(ContextCompat.getColor(requireContext(), orderDetailsUI.status.color))
            statusImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), orderDetailsUI.status.image))
            price.setPriceText(orderDetailsUI.totalPrice)
            deliveryDate.text = orderDetailsUI.dateDelivery
            deliveryInterval.text = orderDetailsUI.deliveryTimeInterval
            payMethod.text = orderDetailsUI.payMethod
            status.text = orderDetailsUI.status.statusName
            status.setTextColor(ContextCompat.getColor(requireContext(), orderDetailsUI.status.color))
            address.text = orderDetailsUI.address
            receiverName.text = StringBuilder()
                .append(orderDetailsUI.userFirstName)
                .append(" ")
                .append(orderDetailsUI.userSecondName)
            receiverPhone.text = orderDetailsUI.userPhone
            productsPrice.setPriceText(orderDetailsUI.productsPrice)
            depositPrice.setPriceText(orderDetailsUI.depositPrice)
            deliveryPrice.setPriceText(orderDetailsUI.deliveryPrice)
            total.setPriceText(orderDetailsUI.totalPrice)
        }
    }

    private fun updateProductsRecycler(productUIList: List<ProductUI>) {
        val diffUtil = ProductDiffUtilCallback(
            oldList = linearProductsAdapter.productUIList,
            newList = productUIList
        )

        DiffUtil.calculateDiff(diffUtil).let { diffResult ->
            linearProductsAdapter.productUIList = productUIList
            diffResult.dispatchUpdatesTo(linearProductsAdapter)
        }
    }

}