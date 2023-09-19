package com.vodovoz.app.feature.bottom.contacts

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.data.model.common.*
import com.vodovoz.app.databinding.FragmentContactsFlowBinding
import com.vodovoz.app.feature.bottom.contacts.adapter.ContactsClickListener
import com.vodovoz.app.feature.bottom.contacts.adapter.controller.EmailController
import com.vodovoz.app.feature.bottom.contacts.adapter.controller.PhoneController
import com.vodovoz.app.ui.model.ChatUI
import com.vodovoz.app.ui.model.EmailUI
import com.vodovoz.app.ui.model.PhoneUI
import com.vodovoz.app.util.extensions.snack
import com.vodovoz.app.util.extensions.startTelegram
import com.vodovoz.app.util.extensions.startViber
import com.vodovoz.app.util.extensions.startWhatsUp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactsFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_contacts_flow

    private val viewModel: ContactsFlowViewModel by viewModels()

    private val binding: FragmentContactsFlowBinding by viewBinding {
        FragmentContactsFlowBinding.bind(
            contentView
        )
    }

    private val emailController = EmailController(getContactsClickListener())
    private val phoneController = PhoneController(getContactsClickListener())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchContacts()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phoneController.bind(binding.phonesRecycler)
        emailController.bind(binding.emailsRecycler)

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

                    if (state.data.item != null) {

                        emailController.submitList(state.data.item.emailUIList)
                        val chatBundle = state.data.item.chatsBundleUI
                        val mappedList = if (chatBundle != null) {
                            state.data.item.phoneUIList + state.data.item.chatsBundleUI
                        } else {
                            state.data.item.phoneUIList
                        }

                        phoneController.submitList(mappedList)
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

    private fun getContactsClickListener() : ContactsClickListener {
        return object : ContactsClickListener {
            override fun onPhoneSelect(item: PhoneUI) {
                when(item.type) {
                    WHATSAPP_TYPE -> {
                        val uri = Uri.parse("https://api.whatsapp.com/send?phone=${item.value}")
                        val sendIntent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(sendIntent)
                    }
                    PHONE_TYPE -> {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", item.value, null))
                        startActivity(intent)
                    }
                }
            }

            override fun onChatIconSelect(item: ChatUI) {
                when(item.type) {
                    WHATSAPP_TYPE -> {
                        requireActivity().startWhatsUp(item.action)
                    }
                    VIBER_TYPE -> {
                        requireActivity().startViber(item.action)
                    }
                    TELEGRAM_TYPE -> {
                        requireActivity().startTelegram(item.action)
                    }
                    CHAT_TYPE -> {
                        findNavController().navigate(ContactsFragmentDirections.actionToWebViewFragment(
                            "http://jivo.chat/mk31km1IlP",
                            "Чат"
                        ))
                    }
                }
            }

            override fun onEmailSelect(item: EmailUI) {
                val emailIntent = Intent(Intent.ACTION_SEND)
                emailIntent.type = "plain/text"
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(item.value))
                startActivity(Intent.createChooser(emailIntent, "Send mail..."))
            }
        }
    }
}
