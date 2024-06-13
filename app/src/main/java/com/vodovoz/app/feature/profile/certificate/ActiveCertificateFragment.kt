package com.vodovoz.app.feature.profile.certificate

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentCertificateBinding
import com.vodovoz.app.util.SpanWithUrlHandler
import com.vodovoz.app.util.extensions.fromHtml
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActiveCertificateFragment : BaseFragment() {

    private val binding: FragmentCertificateBinding by viewBinding {
        FragmentCertificateBinding.bind(contentView)
    }
    internal val viewModel: CertificateFlowViewModel by viewModels()

    override fun layout(): Int = R.layout.fragment_certificate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoad()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButtons()
        observeUiState()
        observeEvents()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel
                    .observeUiState()
                    .collect { state ->
                        if (state.loadingPage) {
                            showLoader()
                        } else {
                            hideLoader()
                        }

                        val bundle = state.data.activateCertificateBundleUI
                        if (bundle != null) {
                            initToolbar(bundle.title)
                            with(binding) {
                                info.text = bundle.details.fromHtml()
                                name.text = bundle.certificatePropertyUIList[0].title
                                value.hint = bundle.certificatePropertyUIList[0].textToField
                                submit.text = bundle.certificatePropertyUIList[0].buttonText
                                submit.backgroundTintList = ColorStateList.valueOf(
                                    Color.parseColor(bundle.certificatePropertyUIList[0].buttonColor)
                                )

                                SpanWithUrlHandler.setTextWithUrl(
                                    bundle.certificatePropertyUIList[0].underButtonText,
                                    binding.underButtonText
                                ) { url, _ ->
                                    if (url != null) {
                                        findNavController().navigate(
                                            ActiveCertificateFragmentDirections.actionToWebViewFragment(
                                                url,
                                                ""
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        showError(state.error)
                    }
            }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel
                    .observeEvent()
                    .collect {
                        when (it) {
                            is CertificateFlowViewModel.CertificateEvents.ActivateResult -> {
                                if (it.title != "Ошибка") {
                                    binding.value.setText("")
                                }
                                AlertDialog.Builder(requireContext())
                                    .setTitle(it.title)
                                    .setMessage(it.message)
                                    .setPositiveButton("ОК") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .show()
                            }
                        }
                    }
            }
        }
    }

    private fun initButtons() {
        binding.submit.setOnClickListener {
            if (binding.value.text.isNotEmpty()) {
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.value.windowToken, 0)
                binding.value.clearFocus()
                viewModel.activateCertificate(binding.value.text.toString())
            }
        }
    }
}