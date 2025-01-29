package com.vodovoz.app.feature.auth.login

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.databinding.FragmentRecoverPasswordFlowBinding
import com.vodovoz.app.feature.auth.login.LoginFlowViewModel.MessageType.Message
import com.vodovoz.app.feature.cart.CartFlowViewModel
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel
import com.vodovoz.app.feature.home.HomeFlowViewModel
import com.vodovoz.app.feature.profile.ProfileFlowViewModel
import com.vodovoz.app.util.FieldValidationsSettings
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RecoverPasswordFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_recover_password_flow

    private val binding: FragmentRecoverPasswordFlowBinding by viewBinding {
        FragmentRecoverPasswordFlowBinding.bind(
            contentView
        )
    }

    @Inject
    lateinit var tabManager: TabManager

    @Inject
    lateinit var accountManager: AccountManager

    private val viewModel: LoginFlowViewModel by viewModels()
    private val profileViewModel: ProfileFlowViewModel by activityViewModels()
    private val flowViewModel: HomeFlowViewModel by activityViewModels()
    private val cartFlowViewModel: CartFlowViewModel by activityViewModels()
    private val favoriteViewModel: FavoriteFlowViewModel by activityViewModels()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
        viewModel.changeAuthType()
        viewModel.checkIfLoginAlready()
        initToolbar(resources.getString(R.string.enter_email_title))

        observeUiState()
        observeEvents()
        viewModel.setupByPhone()
        bindTextListeners()
        bindErrorRefresh { showError(null) }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel
                    .observeEvent()
                    .collect {
                        when (it) {
                            LoginFlowViewModel.LoginEvents.AuthByPhone -> {

                            }

                            LoginFlowViewModel.LoginEvents.AuthByEmail -> {

                            }

                            is LoginFlowViewModel.LoginEvents.AuthError -> {
                                debugLog { "AuthError" }
                                val message = when (it.message) {
                                    is Message -> it.message.param
                                    is LoginFlowViewModel.MessageType.WrongCode -> getString(R.string.wrong_code)
                                    else -> ""
                                }
                                Log.d("PASSWORD", message)
                            }

                            LoginFlowViewModel.LoginEvents.AuthSuccess -> {
                                debugLog { "AuthSuccess" }
                                profileViewModel.refresh()
                                flowViewModel.refresh()
                                cartFlowViewModel.refreshIdle()
                                favoriteViewModel.refreshIdle()
                                val redirect = tabManager.fetchAuthRedirect()
                                if (redirect == TabManager.DEFAULT_AUTH_REDIRECT) {
                                    repeat(2){
                                        findNavController().popBackStack()
                                    }
                                } else {
                                    tabManager.selectTab(redirect)
                                    tabManager.setDefaultAuthRedirect()
                                }
                            }

                            LoginFlowViewModel.LoginEvents.CodeComplete -> {
                                debugLog { "CodeComplete" }
                            }

                            is LoginFlowViewModel.LoginEvents.PasswordRecoverError -> {
                                debugLog { "PasswordRecoverError" }
                                var message = ""
                                when (it.message) {
                                    is LoginFlowViewModel.MessageType.WrongEmail -> {
                                        binding.tvWrongEmail.visibility = View.VISIBLE
                                        binding.etEmail.setBackgroundDrawable(ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.bg_error_edit_text
                                        ))
                                        binding.tvControlLine.visibility = View.GONE
                                    }
                                    is LoginFlowViewModel.MessageType.RepeatError -> message = getString(R.string.error_repeat)
                                    else -> message = ""
                                }
                                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                            }

                            is LoginFlowViewModel.LoginEvents.PasswordRecoverSuccess -> {
                                binding.emailContainer.visibility = View.GONE
                                binding.messageContainer.visibility = View.VISIBLE
                                hideToolbar()
                            }

                            LoginFlowViewModel.LoginEvents.TimerFinished -> {

                            }

                            is LoginFlowViewModel.LoginEvents.TimerTick -> {

                            }

                            is LoginFlowViewModel.LoginEvents.SetupByPhone -> {

                            }
                        }
                    }
            }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeUiState()
                    .collect {

                        if (it.loadingPage) {
                            showLoader()
                        } else {
                            hideLoader()
                        }

                        if (it.data.settings != null) {
                            binding.etEmail.setText(it.data.settings.email)
                        }

                        showError(it.error)
                    }
            }
        }

    }

    private fun validateEmail(): Boolean {
        return FieldValidationsSettings.EMAIL_REGEX.matches(binding.etEmail.text.toString())
    }

    private fun initButtons() {
        binding.btnRecoverPassword.setOnClickListener {
            viewModel.recoverPassword(binding.etEmail.text.toString())
        }

        binding.closeImage.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnOk.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun bindTextListeners() {
        binding.etEmail.doOnTextChanged { text, start, before, count ->
            if (validateEmail()){
                binding.btnRecoverPassword.visibility = View.VISIBLE
                binding.btnRecoverPasswordDisabled.visibility = View.GONE
            }
            else{
                binding.btnRecoverPassword.visibility = View.GONE
                binding.btnRecoverPasswordDisabled.visibility = View.VISIBLE
            }

            binding.tvWrongEmail.visibility = View.GONE
            binding.etEmail.setBackgroundDrawable(ContextCompat.getDrawable(
                requireContext(),
                R.drawable.selector_edit_text
            ))
        }
    }
}
