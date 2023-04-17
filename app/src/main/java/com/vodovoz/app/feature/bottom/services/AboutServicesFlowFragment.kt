package com.vodovoz.app.feature.bottom.services

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentAboutServicesFlowBinding
import com.vodovoz.app.feature.bottom.services.adapter.ServicesClickListener
import com.vodovoz.app.ui.fragment.about_services.AboutServicesDialogFragmentDirections
import com.vodovoz.app.ui.model.ServiceUI
import com.vodovoz.app.util.extensions.fromHtml
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutServicesFlowFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_about_services_flow

    private val viewModel: AboutServicesFlowViewModel by viewModels()

    private val binding: FragmentAboutServicesFlowBinding by viewBinding {
        FragmentAboutServicesFlowBinding.bind(
            contentView
        )
    }

    private val servicesController by lazy {
        ServicesController(getServicesClickListener(), requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoadSorted()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        servicesController.bind(binding.rvServices)
        bindErrorRefresh { viewModel.refreshSorted() }
        observeUiState()
        observeEvents()
    }

    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeEvent()
                .collect {
                    when (it) {
                        is AboutServicesFlowViewModel.AboutServicesEvents.NavigateToDetails -> {
                            findNavController().navigate(
                                AboutServicesDialogFragmentDirections.actionToServiceDetailFragment(
                                    it.typeList.toTypedArray(),
                                    it.type
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect { state ->
                    if (state.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    val detail = state.data.item?.detail ?: ""
                    val title =
                        state.data.item?.title ?: resources.getString(R.string.services_title)

                    binding.tvDetails.text = detail.fromHtml()

                    initToolbar(title)

                    servicesController.submitList(state.data.item?.serviceUIList ?: emptyList())

                    showError(state.error)

                }
        }
    }

    private fun getServicesClickListener(): ServicesClickListener {
        return object : ServicesClickListener {
            override fun onItemClick(item: ServiceUI) {
                viewModel.navigateToDetails(item.type)
            }
        }
    }

}