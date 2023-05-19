package com.vodovoz.app.feature.bottom.services.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.databinding.FragmentServiceDetailsFlowBinding
import com.vodovoz.app.databinding.FragmentServiceDetailsFlowNewBinding
import com.vodovoz.app.feature.bottom.services.AboutServicesFlowViewModel
import com.vodovoz.app.feature.bottom.services.newservs.AboutServicesNewViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServiceDetailNewFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_service_details_flow_new

    private val viewModel: AboutServicesNewViewModel by viewModels()

    private val binding: FragmentServiceDetailsFlowNewBinding by viewBinding {
        FragmentServiceDetailsFlowNewBinding.bind(
            contentView
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchServiceDetailsData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

                    showError(state.error)

                }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(url: String) {
        /*binding.descriptionTv.settings.javaScriptEnabled = true

        try {
            binding.descriptionTv.loadDataWithBaseURL(null, url, "text/html", "utf-8", null)
        } catch (e: Throwable) {
            showError(e.toErrorState())
        }*/
    }

}