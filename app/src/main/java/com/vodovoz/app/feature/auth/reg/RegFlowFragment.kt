package com.vodovoz.app.feature.auth.reg

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentRegisterFlowBinding
import com.vodovoz.app.ui.extensions.TextViewExtensions.setPhoneValidator
import com.vodovoz.app.util.FieldValidationsSettings
import com.vodovoz.app.util.extensions.snack
import com.vodovoz.app.util.extensions.textOrError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegFlowFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_register_flow

    private val binding: FragmentRegisterFlowBinding by viewBinding {
        FragmentRegisterFlowBinding.bind(
            contentView
        )
    }

    private val viewModel: RegFlowViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar("Регистрация")
        initButtons()
        observeEvents()
        observeUiState()
        bindErrorRefresh { showError(null) }
        bindTextListeners()
    }

    private fun initButtons() {
        binding.btnRegister.setOnClickListener {

            val firstName = binding.tilFirstName.textOrError(2) ?: return@setOnClickListener
            val secondName = binding.tilSecondName.textOrError(2) ?: return@setOnClickListener

            val validateEmail = validateEmail()
            if (validateEmail.not()) return@setOnClickListener

            if (FieldValidationsSettings.PHONE_REGEX.matches(it.toString()).not()) return@setOnClickListener

            val password = binding.tilPassword.textOrError(2) ?: return@setOnClickListener

            if (binding.scPersonalInfo.isChecked.not()) {
                requireActivity().snack("Необходимо согласие на обработку персональных данных")
                binding.scPersonalInfo.error = "Необходимо согласие на обработку персональных данных"
                return@setOnClickListener
            }

            viewModel.register(
                firstName = firstName,
                secondName = secondName,
                email = binding.etEmail.text.toString(),
                phone = binding.etPhone.text.toString(),
                password = password
            )
        }
    }

    private fun validateEmail(): Boolean {
        if (!FieldValidationsSettings.EMAIL_REGEX.matches(binding.etEmail.text.toString())) {
            binding.tilEmail.error = "Неправильный формат почты"
            return false
        } else  binding.tilEmail.error = null
        return true
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeUiState()
                .collect {
                    if (it.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    showError(it.error)
                }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeEvent()
                .collect {
                    when(it) {
                        is RegFlowViewModel.RegEvents.RegError -> {
                            Snackbar.make(binding.root, it.message, Snackbar.LENGTH_LONG).show()
                        }
                        is RegFlowViewModel.RegEvents.RegSuccess -> {
                            findNavController().popBackStack() //todo
                        }
                    }
                }
        }
    }

    private fun bindTextListeners() {
        binding.etEmail.doOnTextChanged { _, _,_, count ->
            if (count >0) binding.tilEmail.isErrorEnabled = false
        }

        binding.etPassword.doOnTextChanged { _, _,_, count ->
            if (count >0) binding.tilPassword.isErrorEnabled = false
        }

        binding.etFirstName.doOnTextChanged { _, _,_, count ->
            if (count >0) binding.tilFirstName.isErrorEnabled = false
        }

        binding.etSecondName.doOnTextChanged { _, _,_, count ->
            if (count >0) binding.tilSecondName.isErrorEnabled = false
        }

        binding.etPhone.doOnTextChanged { _, _,_, count ->
            if (count >0) binding.tilPhone.isErrorEnabled = false
        }

        binding.scPersonalInfo.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.scPersonalInfo.error = null
            }
        }

        binding.etPhone.setPhoneValidator {
            when(FieldValidationsSettings.PHONE_REGEX.matches(it.toString())) {
                true -> binding.tilPhone.error = null
                false -> binding.tilPhone.error = "Неверный формат телефона"
            }
        }
    }
}