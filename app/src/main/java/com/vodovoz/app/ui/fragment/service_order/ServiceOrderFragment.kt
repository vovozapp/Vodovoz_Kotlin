package com.vodovoz.app.ui.fragment.service_order

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.databinding.FragmentServiceOrderBinding
import com.vodovoz.app.ui.adapter.ServiceOrderFormFieldsAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication

class ServiceOrderFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentServiceOrderBinding
    private lateinit var viewModel: ServiceOrderViewModel

    private val serviceOrderFormFieldsAdapter = ServiceOrderFormFieldsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        getArgs()
    }

    override fun update() {
        viewModel.fetchData()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[ServiceOrderViewModel::class.java]
    }

    private fun getArgs() {
        ServiceOrderFragmentArgs.fromBundle(requireArguments()).apply {
            viewModel.updateArgs(
                serviceType = serviceType,
                serviceName = serviceName
            )
        }
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentServiceOrderBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initAppBar()
        setupButtons()
        initFieldsRecycler()
        observeViewModel()
    }

    private fun initAppBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.let { noNullActionBar ->
            noNullActionBar.setDisplayHomeAsUpEnabled(true)
            noNullActionBar.setDisplayShowHomeEnabled(true)
            noNullActionBar.title = viewModel.serviceName
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
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Hide -> onStateError("Неизвестная ошибка")
                is ViewState.Loading -> onStateLoading()
                is ViewState.Success -> onStateSuccess()
            }
        }

        viewModel.serviceOrderFormFieldUIListLD.observe(viewLifecycleOwner) { data ->
            serviceOrderFormFieldsAdapter.serviceOrderFormFieldUIList = data
            serviceOrderFormFieldsAdapter.notifyDataSetChanged()
        }

        viewModel.errorMessageLD.observe(viewLifecycleOwner) { errorMessage ->
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
        }

        viewModel.successMessageLD.observe(viewLifecycleOwner) { successMessage ->
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(successMessage)
                .setPositiveButton("Ок") { dialog, _ ->
                    dialog.dismiss()
                    findNavController().popBackStack()
                }
                .show()
        }
    }
}