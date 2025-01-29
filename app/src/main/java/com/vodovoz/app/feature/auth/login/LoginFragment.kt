package com.vodovoz.app.feature.auth.login

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.transition.Visibility
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.agreement.AgreementController
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.databinding.FragmentLoginFlowBinding
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.feature.auth.login.LoginFlowViewModel.MessageType.Message
import com.vodovoz.app.feature.cart.CartFlowViewModel
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel
import com.vodovoz.app.feature.home.HomeFlowViewModel
import com.vodovoz.app.feature.profile.ProfileFlowViewModel
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setExpiredCodeText
import com.vodovoz.app.ui.extensions.TextViewExtensions.setPhoneValidator
import com.vodovoz.app.ui.model.enum.AuthType
import com.vodovoz.app.util.FieldValidationsSettings
import com.vodovoz.app.util.SpanWithUrlHandler
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.snack
import com.vodovoz.app.util.extensions.textOrError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_login_flow

    private val binding: FragmentLoginFlowBinding by viewBinding {
        FragmentLoginFlowBinding.bind(
            contentView
        )
    }

    @Inject
    lateinit var tabManager: TabManager

    @Inject
    lateinit var accountManager: AccountManager

    private val biometricManager by lazy { BiometricManager.from(requireContext()) }

    private val executor: Executor by lazy { ContextCompat.getMainExecutor(requireContext()) }
    private val biometricPrompt: BiometricPrompt by lazy {
        BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    requireActivity().snack(getString(R.string.biometric_fault))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    authByUserSettings()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    requireActivity().snack(getString(R.string.biometric_fault))
                }
            })
    }

    private val promptInfo: BiometricPrompt.PromptInfo by lazy {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()
    }


    private val viewModel: LoginFlowViewModel by viewModels()
    private val profileViewModel: ProfileFlowViewModel by activityViewModels()
    private val flowViewModel: HomeFlowViewModel by activityViewModels()
    private val cartFlowViewModel: CartFlowViewModel by activityViewModels()
    private val favoriteViewModel: FavoriteFlowViewModel by activityViewModels()

    private val biometricResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            if (resultCode == Activity.RESULT_OK) {
                biometricPrompt.authenticate(promptInfo)
                accountManager.saveUseBio(true)
            } else {
                accountManager.saveUseBio(false)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.checkIfLoginAlready()

        initToolbar(resources.getString(R.string.auth_title))
        initButtons()
        initPersonalData()
        observeUiState()
        observeEvents()
        viewModel.setupByPhone()
        binding.etPhone.setPhoneValidator {}
        bindTextListeners()
        bindErrorRefresh { showError(null) }
        checkShowFingerPrint()
        bindFingerPrintBtn()
    }

    private fun initPersonalData() {
        SpanWithUrlHandler.setTextWithUrl(
            text = AgreementController.getText(),
            textView = binding.tvPersonalData
        ) { url, index ->
            findNavController().navigate(
                LoginFragmentDirections.actionToWebViewFragment(
                    url = url ?: "",
                    title = AgreementController.getTitle(index) ?: "",
                )
            )

        }
//        binding.tvPersonalData.setOnClickListener {
//            findNavController().navigate(
//                LoginFragmentDirections.actionToWebViewFragment(
//                    url = ApiConfig.PERSONAL_DATA_URL,
//                    title = "Персональные данные"
//                )
//            )
//        }
    }

    internal fun authByUserSettings() {
        val userSettings = accountManager.fetchUserSettings()
        if (userSettings.email.isNotEmpty() && userSettings.password.isNotEmpty()) {
            viewModel.authByEmail(userSettings.email, userSettings.password)
        }
    }

    private fun bindFingerPrintBtn() {
        binding.btnFingerPrint.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun checkShowFingerPrint() {
        val userSettings = accountManager.fetchUserSettings()
        val isSettingsCorrect =
            userSettings.email.isNotEmpty() && userSettings.password.isNotEmpty()
        if (isSettingsCorrect) checkBiometric()
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
                            }

                            LoginFlowViewModel.LoginEvents.AuthSuccess -> {
                                debugLog { "AuthSuccess" }
                                profileViewModel.refresh()
                                flowViewModel.refresh()
                                cartFlowViewModel.refreshIdle()
                                favoriteViewModel.refreshIdle()
                                val redirect = tabManager.fetchAuthRedirect()
                                if (redirect == TabManager.DEFAULT_AUTH_REDIRECT) {
                                    findNavController().popBackStack()
                                } else {
                                    tabManager.selectTab(redirect)
                                    tabManager.setDefaultAuthRedirect()
                                }
                            }

                            LoginFlowViewModel.LoginEvents.CodeComplete -> {
                                debugLog { "CodeComplete" }
                                binding.tilCode.requestFocus()
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
                                binding.tvExpired.text = getString(R.string.resend_code)
                                binding.tvExpired.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.bluePrimary
                                    )
                                )
                                binding.tvExpired.setTypeface(
                                    ResourcesCompat.getFont(
                                        requireContext(),
                                        R.font.rotonda_normal
                                    ), Typeface.BOLD
                                )
                                binding.tvExpired.setOnClickListener {
                                    //when (FieldValidationsSettings.PHONE_REGEX.matches(binding.etPhone.text.toString())) {
                                    //    true -> viewModel.requestCode(binding.etPhone.text.toString())
                                    //    false -> binding.tilPhone.error =
                                    //        getString(R.string.wrong_phone)
                                    //}
                                }
                            }

                            is LoginFlowViewModel.LoginEvents.TimerTick -> {
                                debugLog { "TimerTick" }
                                binding.tvExpired.setExpiredCodeText(it.tick)
                                binding.tvExpired.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.text_gray
                                    )
                                )
                                binding.tvExpired.typeface =
                                    ResourcesCompat.getFont(requireContext(), R.font.rotonda_normal)
                                binding.tvExpired.visibility = View.VISIBLE
                                binding.btnSignIn.text = getString(R.string.enter)
                                binding.tilCode.visibility = View.VISIBLE
                            }

                            is LoginFlowViewModel.LoginEvents.SetupByPhone -> {
                                debugLog { "SetupByPhone" }
                                setupAuthByPhone(it.time, it.phone)
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

                        if (!it.data.lastAuthPhone.isNullOrEmpty()) {
                            binding.etPhone.setText(it.data.lastAuthPhone)
                        }

                        showError(it.error)
                    }
            }
        }

    }

    private fun initButtons() {
        with(binding){
            cbPersonalInfo.setOnClickListener {
                if(!FieldValidationsSettings.PHONE_REGEX.matches(binding.etPhone.text.toString())){
                    binding.btnSignIn.visibility = View.GONE
                    binding.btnSignInDisabled.visibility = View.VISIBLE
                    return@setOnClickListener
                }
                binding.btnSignIn.visibility = if(cbPersonalInfo.isChecked ) View.VISIBLE else View.GONE
                binding.btnSignInDisabled.visibility = if(cbPersonalInfo.isChecked) View.GONE else View.VISIBLE
            }
            btnSignIn.setOnClickListener {
                when (FieldValidationsSettings.PHONE_REGEX.matches(binding.etPhone.text.toString())) {
                    true -> {
                        findNavController().navigate(LoginFragmentDirections.actionToLoginByPhoneCodeFragment(binding.etPhone.text.toString()))
                    }

                    false -> {
                        debugLog { "Неверный формат телефона" }
                        //binding.tilPhone.error = getString(R.string.wrong_phone)
                    }
                }
            }
            //tvRegister.setOnClickListener {
            //    findNavController().navigate(LoginFragmentDirections.actionToRegisterFragment())
            //}

            tvAuthByEmail.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionToLoginByEmailFragment())
            }
        }
    }

    private fun setupAuthByPhone(expiredTime: Int, phone: String) {

        binding.etPhone.setPhoneValidator {
           // when (FieldValidationsSettings.PHONE_REGEX.matches(it.toString())) {
           //     true -> binding.tilPhone.error = null
           //     false -> binding.tilPhone.error = getString(R.string.wrong_phone)
           // }
        }

        when (expiredTime >= 0) {
            true -> {
                binding.tvExpired.visibility = View.GONE
                binding.tilCode.visibility = View.GONE
            }

            false -> {
                viewModel.startCountDownTimer(-expiredTime)
                if (phone.isNotEmpty()) {
                    binding.etPhone.setText(phone)
                }
                binding.tilCode.requestFocus()
            }
        }
    }

    private fun bindTextListeners() {
        binding.etCode.doOnTextChanged { _, _, _, count ->
            if (count > 0) binding.tilCode.isErrorEnabled = false
        }

        binding.etPhone.doOnTextChanged { phone, _, _, _ ->
            if (phone != null){
                if (FieldValidationsSettings.PHONE_REGEX.matches(phone.toString()) && binding.cbPersonalInfo.isChecked) {
                    binding.btnSignIn.visibility = View.VISIBLE
                    binding.btnSignInDisabled.visibility = View.GONE
                }
                else {
                    binding.btnSignIn.visibility = View.GONE
                    binding.btnSignInDisabled.visibility = View.VISIBLE
                }
            }
        }

        binding.cbPersonalInfo.setOnCheckedChangeListener { _, b ->
            if (b) {
                binding.cbPersonalInfo.error = null
            }
        }
    }

    private fun checkBiometric() {
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                binding.btnFingerPrint.isVisible = true
                biometricPrompt.authenticate(promptInfo)
                accountManager.saveUseBio(true)
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                binding.btnFingerPrint.isVisible = false
                accountManager.saveUseBio(false)
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                binding.btnFingerPrint.isVisible = false
                accountManager.saveUseBio(false)
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                binding.btnFingerPrint.isVisible = false
                accountManager.saveUseBio(false)
                // Prompts the user to create credentials that your app accepts.
                /*val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                }
                biometricResultLauncher.launch(enrollIntent)*/
            }
        }
    }
}

//todo если необходимо
@AndroidEntryPoint
class LoginFragmentCompose : Fragment(R.layout.fragment_login_flow) {

    @Inject
    lateinit var tabManager: TabManager

    @Inject
    lateinit var accountManager: AccountManager

    private val biometricManager by lazy { BiometricManager.from(requireContext()) }

    private val executor: Executor by lazy { ContextCompat.getMainExecutor(requireContext()) }
    private val biometricPrompt: BiometricPrompt by lazy {
        BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    requireActivity().snack(getString(R.string.biometric_fault))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    authByUserSettings()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    requireActivity().snack(getString(R.string.biometric_fault))
                }
            })
    }

    private val promptInfo: BiometricPrompt.PromptInfo by lazy {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()
    }


    private val viewModel: LoginFlowViewModel by viewModels()
    private val profileViewModel: ProfileFlowViewModel by activityViewModels()
    private val flowViewModel: HomeFlowViewModel by activityViewModels()
    private val cartFlowViewModel: CartFlowViewModel by activityViewModels()
    private val favoriteViewModel: FavoriteFlowViewModel by activityViewModels()

    private val biometricResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            if (resultCode == Activity.RESULT_OK) {
                biometricPrompt.authenticate(promptInfo)
                accountManager.saveUseBio(true)
            } else {
                accountManager.saveUseBio(false)
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)
            setContent {
                VodovozTheme {

                }
            }
        }
    }

    internal fun authByUserSettings() {
        val userSettings = accountManager.fetchUserSettings()
        if (userSettings.email.isNotEmpty() && userSettings.password.isNotEmpty()) {
            viewModel.authByEmail(userSettings.email, userSettings.password)
        }
    }

    private fun initPersonalData() {
        // Инициализация кнопки
//        SpanWithUrlHandler.setTextWithUrl(
//            text = AgreementController.getText(),
//            textView = binding.tvPersonalData
//        ) { url, index ->
//            findNavController().navigate(
//                LoginFragmentDirections.actionToWebViewFragment(
//                    url = url ?: "",
//                    title = AgreementController.getTitle(index) ?: "",
//                )
//            )
//        }
    }

}
