package com.vodovoz.app.feature.buy_certificate

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.feature.cart.ordering.OrderingFragment
import com.vodovoz.app.ui.extensions.ViewExtensions.openLink
import com.vodovoz.app.ui.model.PayMethodUI
import com.vodovoz.app.ui.model.custom.OrderingCompletedInfoBundleUI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BuyCertificateFragment : BaseFragment() {
    override fun layout(): Int {
        return 0
    }

    private val viewModel: BuyCertificateViewModel by viewModels()

    @Inject
    lateinit var tabManager: TabManager

    private var job: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentView.visibility = View.GONE
        composeView.visibility = View.VISIBLE
        observeUiState()
        observeEvents()
        observeResultLiveData()
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(OrderingFragment.SELECTED_PAY_METHOD)
            ?.observe(viewLifecycleOwner) { payMethodId ->
                viewModel.setSelectedPaymentMethod(payMethodId)
            }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.observeEvent().collect {
                hideSoftKeyboard()
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

                    is BuyCertificateViewModel.BuyCertificateEvents.AuthError -> {
                        tabManager.selectTab(R.id.graph_profile)
                    }

                    is BuyCertificateViewModel.BuyCertificateEvents.OpenLink -> {
                        view?.openLink(it.url)
                    }
                }
            }
        }
    }

    private fun orderingCompleted(orderingCompletedInfoBundleUI: OrderingCompletedInfoBundleUI) {
        initToolbar("Спасибо за заказ")
        composeView.setContent {
            CompleteOrder(
                bundle = orderingCompletedInfoBundleUI,
                onPayUrl = {
                    viewModel.openLink(it)
                }
            )
        }
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
        job?.cancel()
        job = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeUiState().collect { state ->
                    showLoaderWithBg(state.loadingPage)
                    val data = state.data.buyCertificateBundleUI
                    if (data != null) {
                        initToolbar(data.title)

                        composeView.setContent {
                            BuyCertificateScreen(
                                state = data,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
