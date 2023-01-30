package com.vodovoz.app.ui.fragment.contacts

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.data.model.common.*
import com.vodovoz.app.databinding.FragmentContactsBinding
import com.vodovoz.app.ui.adapter.EmailContactsAdapter
import com.vodovoz.app.ui.adapter.PhoneContactsAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import dagger.hilt.android.AndroidEntryPoint

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
        (requireActivity() as AppCompatActivity).let { appCompatActivity ->
            appCompatActivity.setSupportActionBar(binding.toolbar)
            appCompatActivity.supportActionBar?.setDisplayShowHomeEnabled(true)
            appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.contentContainer.setScrollElevation(binding.appBar)
    }

    private fun initPhonesRecycler() {
        binding.phonesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.phonesRecycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.phonesRecycler.adapter = phoneContactsAdapter
    }

    private fun initEmailsRecycler() {
        binding.emailsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.emailsRecycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
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

}