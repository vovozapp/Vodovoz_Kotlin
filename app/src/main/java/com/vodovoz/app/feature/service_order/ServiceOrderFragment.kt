package com.vodovoz.app.feature.service_order

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vodovoz.app.R
import com.vodovoz.app.common.agreement.AgreementController
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentServiceOrderBinding
import com.vodovoz.app.feature.service_order.adapter.ServiceOrderFormFieldsAdapter
import com.vodovoz.app.util.SpanWithUrlHandler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ServiceOrderFragment : BaseFragment() {

    override fun layout() = R.layout.fragment_service_order

    private val binding: FragmentServiceOrderBinding by viewBinding {
        FragmentServiceOrderBinding.bind(
            contentView
        )
    }

    private val viewModel: ServiceOrderViewModel by viewModels()

    private val serviceOrderFormFieldsAdapter = ServiceOrderFormFieldsAdapter()

    private val args: ServiceOrderFragmentArgs by navArgs()

//    private val serviceName =
//        findNavController().currentBackStackEntry?.savedStateHandle?.get<String>("serviceName")
//            ?: ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAppBar()
        setupButtons()
        initFieldsRecycler()
        observeViewModel()
        viewModel.fetchData()
    }

    private fun initAppBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.let { noNullActionBar ->
            noNullActionBar.setDisplayHomeAsUpEnabled(true)
            noNullActionBar.setDisplayShowHomeEnabled(true)
            noNullActionBar.title = args.serviceName
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupButtons() {
        binding.order.setOnClickListener {
            if (!checkValid()) {
                serviceOrderFormFieldsAdapter.notifyDataSetChanged()
                return@setOnClickListener
            }
            val value = StringBuilder()
            serviceOrderFormFieldsAdapter.serviceOrderFormFieldUIList.forEach {
                value.append(it.id).append("$").append(it.value.trim()).append(";")
            }
            viewModel.orderService(value.toString())
        }

        SpanWithUrlHandler.setTextWithUrl(
            text = AgreementController.getText(),
            textView = binding.tvPersonalData
        ) { url, index ->
            findNavController().navigate(
                ServiceOrderFragmentDirections.actionToWebViewFragment(
                    url = url ?: "",
                    title = AgreementController.getTitle(index) ?: "",
                )
            )

        }
    }

    private fun checkValid(): Boolean {
        var valid = true
        serviceOrderFormFieldsAdapter.serviceOrderFormFieldUIList.forEach {
            if (it.isRequired && it.value.isEmpty()) {
                valid = false
                it.isError = true
            }
        }
        return valid
    }

    private fun initFieldsRecycler() {
        binding.fieldsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.fieldsRecycler.adapter = serviceOrderFormFieldsAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.observeUiState().collect { state ->
                if (state.loadingPage) {
                    showLoaderWithBg(true)
                } else {
                    showLoaderWithBg(false)
                }

                val serviceOrderFormFieldUIList = state.data.serviceOrderFormFieldUIListMLD
                if (serviceOrderFormFieldUIList.isNotEmpty()) {
                    serviceOrderFormFieldsAdapter.serviceOrderFormFieldUIList =
                        serviceOrderFormFieldUIList
                    serviceOrderFormFieldsAdapter.notifyDataSetChanged()
                }

                val successMessage = state.data.successMessageMLD
                if (successMessage.isNotEmpty()) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(successMessage)
                        .setPositiveButton("Ок") { dialog, _ ->
                            dialog.dismiss()
                            findNavController().popBackStack()
                        }
                        .show()
                }

                showError(state.error)
            }
        }
    }
}