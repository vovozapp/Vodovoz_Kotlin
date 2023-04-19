package com.vodovoz.app.feature.bottom.contacts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentContactsFlowBinding
import com.vodovoz.app.util.extensions.snack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactsFlowFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_contacts_flow

    private val viewModel: ContactsFlowViewModel by viewModels()

    private val binding: FragmentContactsFlowBinding by viewBinding {
        FragmentContactsFlowBinding.bind(
            contentView
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchContacts()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar("Связаться с нами")
        initWriteUsButton()
        observeUiState()
        observeEvents()
    }

    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeEvent()
                .collect {
                    when (it) {
                        is ContactsFlowViewModel.ContactsEvents.SendEmailSuccess -> {
                            requireActivity().snack(it.message)
                        }
                        is ContactsFlowViewModel.ContactsEvents.SendEmailError -> {
                            requireActivity().snack(it.message)
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


                    showError(state.error)

                }
        }
    }

    private fun initWriteUsButton() {
        binding.writeUsContainer.submit.setOnClickListener { writeUs() }
    }

    private fun writeUs() {
        if (!binding.writeUsContainer.personalDataSwitch.isChecked) {
            requireActivity().snack("Подтвердите согласие на обработку данных!")
            return
        }
        val name = binding.writeUsContainer.name.text.toString()
        val phone = binding.writeUsContainer.phone.text.toString()
        val email = binding.writeUsContainer.email.text.toString()
        val descriptions = binding.writeUsContainer.description.text.toString()

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || descriptions.isEmpty()) {
            requireActivity().snack("Заполните все поля")
            return
        }

        viewModel.sendMail(name, phone, email, descriptions)
    }
}