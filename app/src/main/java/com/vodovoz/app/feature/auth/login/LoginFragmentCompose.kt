package com.vodovoz.app.feature.auth.login

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.agreement.AgreementController
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.navigation.navigateToLogin
import com.vodovoz.app.core.navigation.navigateToLoginByEmail
import com.vodovoz.app.core.navigation.navigateToRegister
import com.vodovoz.app.core.navigation.navigateToWebView
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.feature.auth.login.LoginFlowViewModel.MessageType.Message
import com.vodovoz.app.feature.cart.CartFlowViewModel
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel
import com.vodovoz.app.feature.home.HomeFlowViewModel
import com.vodovoz.app.feature.profile.ProfileFlowViewModel
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.snack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

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
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)
            setContent {
                VodovozTheme {
                    val pagingState by viewModel.observeUiState().collectAsStateWithLifecycle()
                    val viewState by rememberUpdatedState(pagingState.data)

                    when (viewState.uiState) {
                        LoginFlowViewModel.LoginUiState.Error -> {
                            NetworkErrorPlaceholder { viewModel.fetchLoginDetails() }
                        }

                        LoginFlowViewModel.LoginUiState.Loading -> {
                            LoadingPlaceholder()
                        }

                        LoginFlowViewModel.LoginUiState.Success -> {
                            LoginScreen(viewModel = viewModel, viewState = viewState)
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeEvents()
        checkShowFingerPrint()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.checkIfLoginAlready()
        viewModel.setupByPhone()
    }

    internal fun authByUserSettings() {
        val userSettings = accountManager.fetchUserSettings()
        if (userSettings.email.isNotEmpty() && userSettings.password.isNotEmpty()) {
            viewModel.authByEmail(userSettings.email, userSettings.password)
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
                    .collect { events ->
                        when (events) {
                            LoginFlowViewModel.LoginEvents.AuthByPhone -> {

                            }

                            LoginFlowViewModel.LoginEvents.AuthByEmail -> {

                            }

                            is LoginFlowViewModel.LoginEvents.AuthError -> {
                                debugLog { "AuthError" }
                                val message = when (events.message) {
                                    is Message -> events.message.param
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
                            }

                            is LoginFlowViewModel.LoginEvents.PasswordRecoverError -> {
                                debugLog { "PasswordRecoverError" }
                                val message = when (events.message) {
                                    is LoginFlowViewModel.MessageType.WrongEmail -> getString(R.string.wrong_email)
                                    is LoginFlowViewModel.MessageType.RepeatError -> getString(R.string.error_repeat)
                                    else -> ""
                                }
                            }

                            is LoginFlowViewModel.LoginEvents.PasswordRecoverSuccess -> {
                                debugLog { "PasswordRecoverSuccess" }
                                val param = when (events.message) {
                                    is Message -> events.message.param
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

                            }

                            is LoginFlowViewModel.LoginEvents.TimerTick -> {
                                debugLog { "TimerTick" }

                            }

                            is LoginFlowViewModel.LoginEvents.SetupByPhone -> {
                                debugLog { "SetupByPhone" }

                            }

                            LoginFlowViewModel.LoginEvents.GoBack -> {
                                findNavController().popBackStack()
                            }

                            is LoginFlowViewModel.LoginEvents.GoToWebView -> {
                                findNavController().navigateToWebView(
                                    url = events.url,
                                    title = events.title,
                                )
                            }

                            LoginFlowViewModel.LoginEvents.GoToLoginByEmail -> {
                                findNavController().navigateToLoginByEmail()
                            }

                            LoginFlowViewModel.LoginEvents.GoToRegister -> {
                                findNavController().navigateToRegister()
                            }
                        }
                    }
            }
        }
    }


    private fun checkBiometric() {
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                biometricPrompt.authenticate(promptInfo)
                accountManager.saveUseBio(true)
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                accountManager.saveUseBio(false)
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                accountManager.saveUseBio(false)
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                accountManager.saveUseBio(false)

                //todo - did as old app
                // Prompts the user to create credentials that your app accepts.
                /*val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                }
                biometricResultLauncher.launch(enrollIntent)*/
            }

            else -> {
                accountManager.saveUseBio(false)
            }
        }
    }
}