package com.vodovoz.app.feature.bottom.services.newservs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.databinding.FragmentAboutServicesFlowNewBinding
import com.vodovoz.app.feature.bottom.services.ServicesController
import com.vodovoz.app.feature.bottom.services.adapter.ServicesClickListener
import com.vodovoz.app.feature.bottom.services.newservs.model.ServiceNew
import com.vodovoz.app.ui.model.ServiceUI
import com.vodovoz.app.util.extensions.fromHtml
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutServicesNewFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_about_services_flow_new

    private val viewModel: AboutServicesNewViewModel by viewModels()

    private val binding: FragmentAboutServicesFlowNewBinding by viewBinding {
        FragmentAboutServicesFlowNewBinding.bind(
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

                    val description = state.data.item?.aboutServicesData?.description ?: ""
                    val title = state.data.item?.aboutServicesData?.title ?: resources.getString(R.string.services_title)

                    initWebView(description)

                    initToolbar(title)

                    servicesController.submitList(state.data.item?.aboutServicesData?.servicesList ?: emptyList())

                    showError(state.error)

                }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(url: String) {
        binding.descriptionTv.settings.javaScriptEnabled = true

        try {
            binding.descriptionTv.loadDataWithBaseURL(null, url, "text/html", "utf-8", null)
        } catch (e: Throwable) {
            showError(e.toErrorState())
        }
    }

    private fun getServicesClickListener(): ServicesClickListener {
        return object : ServicesClickListener {
            override fun onItemClick(item: ServiceNew) {

            }
        }
    }


}