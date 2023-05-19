package com.vodovoz.app.feature.bottom.services.new

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentAboutServicesFlowNewBinding
import com.vodovoz.app.util.extensions.fromHtml
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutServicesNewFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_about_services_flow_new

    private val viewModel: AboutServicesNewViewModel by activityViewModels()

    private val binding: FragmentAboutServicesFlowNewBinding by viewBinding {
        FragmentAboutServicesFlowNewBinding.bind(
            contentView
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoadSorted()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

                    binding.descriptionTv.text = description.fromHtml()

                    initToolbar(title)

                    showError(state.error)

                }
        }
    }


}