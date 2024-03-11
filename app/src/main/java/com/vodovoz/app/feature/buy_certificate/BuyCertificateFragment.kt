package com.vodovoz.app.feature.buy_certificate

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentBuyCertificateBinding
import com.vodovoz.app.feature.buy_certificate.adapter.BuyCertificateAdapter
import com.vodovoz.app.feature.cart.ordering.OrderingFragment
import com.vodovoz.app.ui.extensions.ViewExtensions.openLink
import com.vodovoz.app.ui.model.PayMethodUI
import com.vodovoz.app.ui.model.custom.BuyCertificatePaymentUI
import com.vodovoz.app.ui.model.custom.OrderingCompletedInfoBundleUI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BuyCertificateFragment : BaseFragment() {
    override fun layout(): Int {
        return R.layout.fragment_buy_certificate
    }

    private val binding: FragmentBuyCertificateBinding by viewBinding {
        FragmentBuyCertificateBinding.bind(
            contentView
        )
    }

    private val viewModel: BuyCertificateViewModel by viewModels()

    private val adapter by lazy {
        BuyCertificateAdapter(viewModel)
    }

//    private val buyCertificateController by lazy {
//        BuyCertificateController(viewModel)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.buyCertificateRecycler.setHasFixedSize(true)
        binding.buyCertificateRecycler.adapter = adapter
        binding.buyCertificateRecycler.layoutManager = LinearLayoutManager(requireContext())

        binding.tvPayMethod.setOnClickListener {
            viewModel.showPaymentMethods()
        }

        binding.submit.setOnClickListener {
            viewModel.buyCertificate()
        }

        observeUiState()
        observeEvents()
        observeResultLiveData()
        viewModel.getBuyCertificateBundle()

    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(OrderingFragment.SELECTED_PAY_METHOD)
            ?.observe(viewLifecycleOwner) { payMethodId ->

                viewModel.setSelectedPaymentMethod(payMethodId)

                if (payMethodId == 1L) {
                    binding.etInputCash.visibility = View.VISIBLE
                    binding.mtBetweenPayMethodAndInputCash.visibility = View.VISIBLE
                } else {
                    binding.etInputCash.visibility = View.GONE
                    binding.mtBetweenPayMethodAndInputCash.visibility = View.GONE
                    binding.tvNamePayMethod.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.text_black
                        )
                    )
                    binding.etInputCash.text = null
                }
            }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.observeEvent().collect {
                when (it) {
                    is BuyCertificateViewModel.BuyCertificateEvents.ShowPaymentMethod -> {
                        showPayMethodPopup(
                            payMethodUIList = it.list,
                            selectedPayMethodId = it.selectedPayMethodId ?: it.list.first().id
                        )
                    }

                    is BuyCertificateViewModel.BuyCertificateEvents.OrderSuccess -> {
                        orderingCompleted(it.data)
                    }
                }
            }
        }
    }

    private fun orderingCompleted(orderingCompletedInfoBundleUI: OrderingCompletedInfoBundleUI) {
        binding.nsvBuyCertificate.visibility = View.INVISIBLE
        binding.tvOrderId.text = orderingCompletedInfoBundleUI.message
        binding.llOrderingCompleted.visibility = View.VISIBLE
        when (orderingCompletedInfoBundleUI.paymentURL.isNotEmpty()) {
            true -> {
                binding.btnGoToPayment.visibility = View.VISIBLE
                binding.btnGoToPayment.setOnClickListener {
                    binding.root.openLink(
                        orderingCompletedInfoBundleUI.paymentURL
                    )
                }
            }

            false -> binding.btnGoToPayment.visibility = View.INVISIBLE
        }
        initToolbar("Спасибо за заказ")
    }

    private fun showPayMethodPopup(payMethodUIList: List<PayMethodUI>, selectedPayMethodId: Long) {
        if (findNavController().currentDestination?.id == R.id.buyCertificateFragment) {
            findNavController().navigate(
                BuyCertificateFragmentDirections.actionToPayMethodSelectionBS(
                    payMethodUIList.toTypedArray(),
                    selectedPayMethodId
                )
            )
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeUiState().collect { state ->
                    showLoaderWithBg(state.loadingPage)
                    val data = state.data.buyCertificateBundleUI
                    if (data != null) {
                        initToolbar(state.data.buyCertificateBundleUI.title)

                        adapter.submitList(data.buyCertificatePropertyUIList)

                        initPayment(state.data.buyCertificateBundleUI.payment)
                    }
                }
            }
        }
    }

    private fun initPayment(data: BuyCertificatePaymentUI) {
        var addStar = ""
        if (data.required) {
            addStar = "*"
        }
        binding.tvNamePayMethod.text = buildString {
            append(data.name)
            append(addStar)
        }
        val text = data.payMethods.firstOrNull { it.isSelected }?.name ?: ""
        binding.tvPayMethod.text = text
    }
}