package com.vodovoz.app.feature.auth.login

import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.vodovoz.app.databinding.FragmentLoginByEmailFlowBinding
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
class LoginByEmailFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_login_by_email_flow

    private val binding: FragmentLoginByEmailFlowBinding by viewBinding {
        FragmentLoginByEmailFlowBinding.bind(
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
                                val email = validateEmail()
                                if (!email) return@collect
                                val password = binding.etPassword.text.toString()
                                debugLog { "AuthByEmail ${binding.etEmail.text.toString()}" }
                                viewModel.authByEmail(
                                    binding.etEmail.text.toString(),
                                    password
                                )
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
                                    repeat(2) {
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
                                val message = when (it.message) {
                                    is LoginFlowViewModel.MessageType.WrongEmail -> getString(R.string.wrong_email)
                                    is LoginFlowViewModel.MessageType.RepeatError -> getString(R.string.error_repeat)
                                    else -> ""
                                }
                                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                            }

                            is LoginFlowViewModel.LoginEvents.PasswordRecoverSuccess -> {
                                debugLog { "PasswordRecoverSuccess" }
                                val param = when (it.message) {
                                    is Message -> it.message.param
                                    else -> ""
                                }
                                MaterialAlertDialogBuilder(requireContext())
                                    .setMessage(getString(R.string.password_recover_success, param))
                                    .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .show()
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
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.observeUiState()
                    .collect {

                        if (it.loadingPage) {
                            showLoader()
                        } else {
                            hideLoader()
                        }

                        val dataSettings = it.data.settings
                        if (dataSettings != null && dataSettings.password.isNotEmpty() && dataSettings.email.isNotEmpty()) {
                            binding.etEmail.setText(it.data.settings.email)
                            binding.etPassword.setText(it.data.settings.password)
                        }

                        initButtons("")
                        showError(it.error)
                    }
            }
        }

    }

    private fun validateEmail(): Boolean {
        if (!FieldValidationsSettings.EMAIL_REGEX.matches(binding.etEmail.text.toString())) {
            binding.etEmail.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_error_edit_text
                )
            )
            binding.tvWrongEmail.visibility = View.VISIBLE
            return false
        } else {
            binding.tvWrongEmail.visibility = View.GONE
            return true
        }
    }

    private fun initButtons(password: String) {
        binding.btnAuth.setOnClickListener {
            val currentPassword = binding.etPassword.text.toString()
            if (currentPassword.length in 6..30 && currentPassword.all { it != ' ' }) {
                viewModel.signIn()
            } else {
                binding.tvWrongPassword.visibility = View.VISIBLE
                binding.etPassword.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_error_edit_text
                    )
                )
                hideAuthButton()
            }

            binding.tvRecoverPassword.setOnClickListener {
                findNavController().navigate(LoginByEmailFragmentDirections.actionToRecoverPasswordFragment())
            }
        }
    }

    private fun bindTextListeners() {
        binding.etEmail.doOnTextChanged { text, start, before, count ->
            binding.tvWrongEmail.visibility = View.GONE
            binding.etEmail.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.selector_edit_text
                )
            )
            showAuthButton()

        }

        binding.etPassword.doOnTextChanged { text, start, before, count ->
            binding.tvWrongPassword.visibility = View.GONE
            binding.etPassword.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.selector_edit_text
                )
            )

            showAuthButton()
        }
    }

    private fun showAuthButton() {
        binding.btnAuth.visibility = View.VISIBLE
        binding.btnAuthDisabled.visibility = View.GONE
    }

    private fun hideAuthButton() {
        binding.btnAuth.visibility = View.GONE
        binding.btnAuthDisabled.visibility = View.VISIBLE
    }
}
