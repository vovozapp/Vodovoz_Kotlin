package com.vodovoz.app.feature.auth.login

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.databinding.FragmentLoginByPhoneCodeFlowBinding
import com.vodovoz.app.feature.auth.login.LoginFlowViewModel.MessageType.Message
import com.vodovoz.app.feature.cart.CartFlowViewModel
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel
import com.vodovoz.app.feature.home.HomeFlowViewModel
import com.vodovoz.app.feature.profile.ProfileFlowViewModel
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setExpiredCodeText
import com.vodovoz.app.ui.extensions.TextViewExtensions.setPhoneValidator
import com.vodovoz.app.util.FieldValidationsSettings
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginByPhoneCodeFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_login_by_phone_code_flow

    private val binding: FragmentLoginByPhoneCodeFlowBinding by viewBinding {
        FragmentLoginByPhoneCodeFlowBinding.bind(
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

    private val args: LoginByPhoneCodeFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.requestCode(args.phoneNumber)
        viewModel.checkIfLoginAlready()
        initToolbar(resources.getString(R.string.enter_code_title))
        initButtons()
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
                                debugLog { "authByPhone" }
                                val code = "${binding.etCode1.text}${binding.etCode2.text}${binding.etCode3.text}${binding.etCode4.text}"
                                viewLifecycleOwner.lifecycleScope.launch {
                                    viewModel.authByPhone(
                                        phone = args.phoneNumber,
                                        code = code
                                    ).join()
                                    delay(700L)
                                    binding.etCode1.requestFocus()
                                    binding.etCode1.setText("")
                                    binding.etCode2.setText("")
                                    binding.etCode3.setText("")
                                    binding.etCode4.setText("")
                                }
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
                                debugLog { "TimerFinished" }
                                binding.tvExpiredText.visibility = View.INVISIBLE
                                binding.tvExpired.text = getString(R.string.resend_code)
                                binding.tvExpired.setTypeface(
                                    ResourcesCompat.getFont(
                                        requireContext(),
                                        R.font.rotonda_normal
                                    ), Typeface.BOLD
                                )
                                binding.tvExpired.setOnClickListener {
                                    when (FieldValidationsSettings.PHONE_REGEX.matches(args.phoneNumber)) {
                                        true -> {
                                            binding.tvExpiredText.visibility = View.VISIBLE
                                            viewModel.requestCode(args.phoneNumber)
                                        }
                                        false -> binding.tvExpired.text =
                                            getString(R.string.wrong_phone)
                                    }
                                }
                            }

                            is LoginFlowViewModel.LoginEvents.TimerTick -> {
                                debugLog { "TimerTick" }
                                binding.tvExpired.setExpiredCodeText(it.tick)
                                binding.tvExpired.visibility = View.VISIBLE
                            }

                            is LoginFlowViewModel.LoginEvents.SetupByPhone -> {
                                setupAuthByPhone(it.time)
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

                        showError(it.error)
                    }
            }
        }

    }

    private fun setupAuthByPhone(expiredTime: Int) {
        when (expiredTime >= 0) {
            true -> {
                binding.tvExpired.visibility = View.GONE
            }
            false -> {
                viewModel.startCountDownTimer(-expiredTime)
                binding.etCode1.requestFocus()
            }
        }
    }

    private fun initButtons() {
        val enterCodeText = "${resources.getString(R.string.enter_code)} ${args.phoneNumber}"
        binding.tvPhoneNumber.text = enterCodeText
    }

    private fun bindTextListeners() {
        binding.etCode1.doOnTextChanged { _, _, _, count ->
            if (count > 0){
                binding.etCode1.setBackgroundDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_round_corner_new_product_blue_small))
                binding.etCode2.requestFocus()
            }
            else binding.etCode1.setBackgroundDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_round_corner_blue_small))
        }

        binding.etCode2.doOnTextChanged { _, _, _, count ->
            if (count > 0){
                binding.etCode2.setBackgroundDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_round_corner_new_product_blue_small))
                binding.etCode3.requestFocus()
            }
            else binding.etCode2.setBackgroundDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_round_corner_blue_small))
        }

        binding.etCode3.doOnTextChanged { _, _, _, count ->
            if (count > 0){
                binding.etCode3.setBackgroundDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_round_corner_new_product_blue_small))
                binding.etCode4.requestFocus()
            }
            else binding.etCode3.setBackgroundDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_round_corner_blue_small))
        }

        binding.etCode4.doOnTextChanged { _, _, _, count ->
            if (count > 0){
                binding.etCode4.setBackgroundDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_round_corner_new_product_blue_small))
                viewModel.signIn()
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(3000)
                    binding.etCode1.requestFocus()
                    binding.etCode1.setText("")
                    binding.etCode2.setText("")
                    binding.etCode3.setText("")
                    binding.etCode4.setText("")
                }
            }
            else binding.etCode4.setBackgroundDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_round_corner_blue_small))
        }
    }
}
