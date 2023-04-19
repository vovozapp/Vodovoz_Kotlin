package com.vodovoz.app.ui.fragment.contacts

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.data.model.common.*
import com.vodovoz.app.databinding.FragmentContactsBinding
import com.vodovoz.app.databinding.FragmentContactsFlowBinding
import com.vodovoz.app.feature.bottom.contacts.ContactsFlowViewModel
import com.vodovoz.app.feature.bottom.contacts.adapter.ContactsClickListener
import com.vodovoz.app.feature.bottom.contacts.adapter.controller.EmailController
import com.vodovoz.app.feature.bottom.contacts.adapter.controller.PhoneController
import com.vodovoz.app.ui.adapter.EmailContactsAdapter
import com.vodovoz.app.ui.adapter.PhoneContactsAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import com.vodovoz.app.ui.model.ChatUI
import com.vodovoz.app.ui.model.EmailUI
import com.vodovoz.app.ui.model.PhoneUI
import com.vodovoz.app.util.extensions.snack
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
                        val uri = Uri.parse("https://api.whatsapp.com/send?phone=${item.action}")
                        val sendIntent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(sendIntent)
                    }
                    VIBER_TYPE -> {
                    }
                    TELEGRAM_TYPE -> {}
                    CHAT_TYPE -> {}
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
/*
@AndroidEntryPoint
class ContactsFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentContactsBinding
    private val viewModel: ContactsViewModel by viewModels()

    private val phoneContactsAdapter = PhoneContactsAdapter(
        onPhoneSelect = { phoneUI ->
            when(phoneUI.type) {
                WHATSAPP_TYPE -> {
                    val uri = Uri.parse("https://api.whatsapp.com/send?phone=${phoneUI.value}")
                    val sendIntent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(sendIntent)
                }
                PHONE_TYPE -> {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneUI.value, null))
                    startActivity(intent)
                }
            }
        },
        onChatIconSelect = { chatUI ->
            when(chatUI.type) {
                WHATSAPP_TYPE -> {
                    val uri = Uri.parse("https://api.whatsapp.com/send?phone=${chatUI.action}")
                    val sendIntent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(sendIntent)
                }
                VIBER_TYPE -> {
                }
                TELEGRAM_TYPE -> {}
                CHAT_TYPE -> {}
            }
        }
    )

    private val emailContactsAdapter = EmailContactsAdapter(
        onEmailSelect = { emailUI ->
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "plain/text"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailUI.value))
            startActivity(Intent.createChooser(emailIntent, "Send mail..."))
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchContacts()
    }

    override fun update() {
        viewModel.fetchContacts()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup,
    ) = FragmentContactsBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initActionBar()
        initPhonesRecycler()
        initEmailsRecycler()
        initWriteUsButton()
        observeViewModel()
    }

    private fun initActionBar() {
        binding.incAppBar.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.incAppBar.tvTitle.text = "Связаться с нами"
    }

    private fun initPhonesRecycler() {
        binding.phonesRecycler.layoutManager = LinearLayoutManager(requireContext())
        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.bg_border_gray)
            ?.let { divider.setDrawable(it) }
        binding.phonesRecycler.addItemDecoration(divider)
        binding.phonesRecycler.adapter = phoneContactsAdapter
    }

    private fun initEmailsRecycler() {
        binding.emailsRecycler.layoutManager = LinearLayoutManager(requireContext())
        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.bg_border_gray)
            ?.let { divider.setDrawable(it) }
        binding.emailsRecycler.addItemDecoration(divider)
        binding.emailsRecycler.adapter = emailContactsAdapter
    }

    private fun initWriteUsButton() {
        binding.writeUsContainer.submit.setOnClickListener { writeUs() }
    }

    private fun writeUs() {
        if (!binding.writeUsContainer.personalDataSwitch.isChecked) {
            Snackbar.make(binding.root, "Подтвердите согласие на обработку данных!", Toast.LENGTH_LONG).show()
            return
        }
        val name = binding.writeUsContainer.name.text.toString()
        val phone = binding.writeUsContainer.phone.text.toString()
        val email = binding.writeUsContainer.email.text.toString()
        val descriptions = binding.writeUsContainer.description.text.toString()

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || descriptions.isEmpty()) {
            Snackbar.make(binding.root, "Заполните все поля", Toast.LENGTH_LONG).show()
            return
        }

        viewModel.sendMail(name, phone, email, descriptions)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { viewState ->
            when(viewState) {
                is ViewState.Hide -> onStateHide()
                is ViewState.Error -> onStateError(viewState.errorMessage)
                is ViewState.Loading -> onStateLoading()
                is ViewState.Success -> onStateSuccess()
            }
        }

        viewModel.contactBundleUILD.observe(viewLifecycleOwner) { contactsBundleUI ->
            emailContactsAdapter.emailUIList = contactsBundleUI.emailUIList
            phoneContactsAdapter.phoneUIList = contactsBundleUI.phoneUIList
            phoneContactsAdapter.chatsBundleUI = contactsBundleUI.chatsBundleUI
            emailContactsAdapter.notifyDataSetChanged()
            phoneContactsAdapter.notifyDataSetChanged()
        }

        viewModel.messageLD.observe(viewLifecycleOwner) { message ->
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        }
    }

}*/
