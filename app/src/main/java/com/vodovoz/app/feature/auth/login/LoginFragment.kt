package com.vodovoz.app.feature.auth.login

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.databinding.FragmentLoginFlowBinding
import com.vodovoz.app.feature.cart.CartFlowViewModel
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel
import com.vodovoz.app.feature.home.HomeFlowViewModel
import com.vodovoz.app.feature.profile.ProfileFlowViewModel
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setExpiredCodeText
import com.vodovoz.app.ui.extensions.TextViewExtensions.setPhoneValidator
import com.vodovoz.app.ui.model.enum.AuthType
import com.vodovoz.app.util.FieldValidationsSettings
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.textOrError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_login_flow

    private val binding: FragmentLoginFlowBinding by viewBinding {
        FragmentLoginFlowBinding.bind(
            contentView
        )
    }

    private val viewModel: LoginFlowViewModel by viewModels()
    private val profileViewModel: ProfileFlowViewModel by activityViewModels()
    private val flowViewModel: HomeFlowViewModel by activityViewModels()
    private val cartFlowViewModel: CartFlowViewModel by activityViewModels()
    private val favoriteViewModel: FavoriteFlowViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(resources.getString(R.string.auth_title))
        initButtons()
        observeUiState()
        observeEvents()
        viewModel.setupByPhone()
        binding.etPhone.setPhoneValidator {}
        bindTextListeners()
    }

    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeEvent()
                .collect {
                    when (it) {
                        LoginFlowViewModel.LoginEvents.AuthByPhone -> {
                            when (binding.tilCode.visibility == View.VISIBLE) {
                                true -> {
                                    debugLog { "authByPhone" }
                                    viewModel.authByPhone(
                                        binding.etPhone.text.toString(),
                                        binding.etCode.text.toString()
                                    )
                                }
                                false -> {
                                    when (FieldValidationsSettings.PHONE_REGEX.matches(binding.etPhone.text.toString())) {
                                        true -> {
                                            debugLog { "requestCode" }
                                            viewModel.requestCode(binding.etPhone.text.toString())
                                        }
                                        false -> {
                                            debugLog { "Неверный формат телефона" }
                                            binding.tilPhone.error = "Неверный формат телефона"
                                        }
                                    }
                                }
                            }
                        }
                        LoginFlowViewModel.LoginEvents.AuthByEmail -> {
                            val email = validateEmail()
                            if (!email) return@collect
                            val password = binding.tilPassword.textOrError(3) ?: return@collect
                            debugLog { "AuthByEmail" }
                            viewModel.authByEmail(
                                binding.etEmail.text.toString(),
                                password
                            )
                        }
                        is LoginFlowViewModel.LoginEvents.AuthError -> {
                            debugLog { "AuthError" }
                            binding.tilPassword.error = it.message
                        }
                        LoginFlowViewModel.LoginEvents.AuthSuccess -> {
                            debugLog { "AuthSuccess" }
                            flowViewModel.refresh()
                            cartFlowViewModel.refreshIdle()
                            favoriteViewModel.refreshIdle()
                            profileViewModel.refreshIdle()
                            findNavController().popBackStack()
                        }
                        LoginFlowViewModel.LoginEvents.CodeComplete -> {
                            debugLog { "CodeComplete" }
                            binding.tilCode.requestFocus()
                        }
                        is LoginFlowViewModel.LoginEvents.PasswordRecoverError -> {
                            debugLog { "PasswordRecoverError" }
                            Snackbar.make(binding.root, it.message, Snackbar.LENGTH_LONG).show()
                        }
                        is LoginFlowViewModel.LoginEvents.PasswordRecoverSuccess -> {
                            debugLog { "PasswordRecoverSuccess" }
                            MaterialAlertDialogBuilder(requireContext())
                                .setMessage(it.message)
                                .setPositiveButton("Ок") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .show()
                        }
                        LoginFlowViewModel.LoginEvents.TimerFinished -> {
                            debugLog { "TimerFinished" }
                            binding.tvExpired.text = "Отправить код повторно"
                            binding.tvExpired.setTextColor(ContextCompat.getColor(requireContext(), R.color.bluePrimary))
                            binding.tvExpired.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.rotonda_normal), Typeface.BOLD)
                            binding.tvExpired.setOnClickListener {
                                when (FieldValidationsSettings.PHONE_REGEX.matches(binding.etPhone.text.toString())) {
                                    true -> viewModel.requestCode(binding.etPhone.text.toString())
                                    false -> binding.tilPhone.error = "Неверный формат телефона"
                                }
                            }
                        }
                        is LoginFlowViewModel.LoginEvents.TimerTick -> {
                            debugLog { "TimerTick" }
                            binding.tvExpired.setExpiredCodeText(it.tick)
                            binding.tvExpired.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_gray))
                            binding.tvExpired.typeface = ResourcesCompat.getFont(requireContext(), R.font.rotonda_normal)
                            binding.tvExpired.visibility = View.VISIBLE
                            binding.btnSignIn.text = "Войти"
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

    private fun validateEmail(): Boolean {
        if (!FieldValidationsSettings.EMAIL_REGEX.matches(binding.etEmail.text.toString())) {
            binding.tilEmail.error = "Неправильный формат почты"
            return false
        } else  binding.tilEmail.error = null
        return true
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect {

                    if (it.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    if (it.data.settings != null) {
                        binding.etEmail.setText(it.data.settings.email)
                        binding.etPassword.setText(it.data.settings.password)
                    }

                    if (it.data.lastAuthPhone != null) {
                        binding.etPhone.setText(it.data.lastAuthPhone)
                    }

                    when (it.data.authType) {
                        AuthType.EMAIL -> {
                            bindEmailBtn()
                        }
                        AuthType.PHONE -> {
                            bindPhoneBtn()
                        }
                    }

                    showError(it.error)
                }
        }

    }

    private fun initButtons() {
        binding.btnSignIn.setOnClickListener {
            viewModel.signIn()
        }
        binding.tvRecoverPassword.setOnClickListener {
            viewModel.recoverPassword(binding.etEmail.text.toString())
        }
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionToRegisterFragment())
        }
        binding.cwAuthByPhoneContainer.setOnClickListener {
            viewModel.changeAuthType()
        }
        binding.cwAuthByEmailContainer.setOnClickListener {
            viewModel.changeAuthType()
        }
    }

    private fun bindEmailBtn() {
        binding.cwAuthByEmailContainer.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        binding.cwAuthByEmailContainer.elevation = resources.getDimension(R.dimen.elevation_3)
        binding.llAutByEmailContainer.visibility = View.VISIBLE
        binding.cwAuthByPhoneContainer.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )
        binding.cwAuthByPhoneContainer.elevation = 0f
        binding.llAutByPhoneContainer.visibility = View.GONE
        binding.btnSignIn.text = "Войти"
    }

    private fun bindPhoneBtn() {
        binding.cwAuthByPhoneContainer.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        binding.cwAuthByPhoneContainer.elevation = resources.getDimension(R.dimen.elevation_3)
        binding.llAutByPhoneContainer.visibility = View.VISIBLE
        binding.cwAuthByEmailContainer.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )
        binding.cwAuthByEmailContainer.elevation = 0f
        binding.llAutByEmailContainer.visibility = View.GONE
        when (binding.tilCode.visibility == View.VISIBLE) {
            true -> binding.btnSignIn.text = "Войти"
            false -> binding.btnSignIn.text = "Выслать код"
        }
    }

    private fun setupAuthByPhone(expiredTime: Int, phone: String) {

        binding.etPhone.setPhoneValidator {
            when(FieldValidationsSettings.PHONE_REGEX.matches(it.toString())) {
                true -> binding.tilPhone.error = null
                false -> binding.tilPhone.error = "Неверный формат телефона"
            }
        }

        when(expiredTime >= 0) {
            true -> {
                binding.tvExpired.visibility = View.GONE
                binding.tilCode.visibility = View.GONE
            }
            false -> {
                viewModel.startCountDownTimer(-expiredTime)
                binding.etPhone.setText(phone)
                binding.tilCode.requestFocus()
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

        binding.etCode.doOnTextChanged { _, _,_, count ->
            if (count >0) binding.tilCode.isErrorEnabled = false
        }

        binding.etPhone.doOnTextChanged { _, _,_, count ->
            if (count >0) binding.tilPhone.isErrorEnabled = false
        }
    }
}
/*
@AndroidEntryPoint
class LoginFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private val profileViewModel: ProfileFlowViewModel by activityViewModels()
    private val flowViewModel: HomeFlowViewModel by activityViewModels()
    private val cartFlowViewModel: CartFlowViewModel by activityViewModels()
    private val favoriteViewModel: FavoriteFlowViewModel by activityViewModels()

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentLoginBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        if (viewModel.isAlreadyLogin()) findNavController().popBackStack()
        initButtons()
        initAppBar()
        observeViewModel()
        onStateSuccess()
        setupAuthByPhone()
    }


    private fun initAppBar() {
        binding.incAppBar.tvTitle.text = resources.getString(R.string.auth_title)
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initButtons() {
        binding.btnSignIn.setOnClickListener {
            when(viewModel.authType) {
                AuthType.EMAIL -> viewModel.validateAuthEmail(binding.etEmail.text.toString(), binding.etPassword.text.toString())
                AuthType.PHONE -> {
                    Toast.makeText(requireContext(), "Auth by Phone", Toast.LENGTH_LONG).show()
                    when(binding.tilCode.visibility == View.VISIBLE) {
                        true -> viewModel.authByPhone(binding.etPhone.text.toString(), binding.etCode.text.toString())
                        false -> requestCode()
                    }
                }
            }
        }
        binding.tvRecoverPassword.setOnClickListener { viewModel.recoverPassword(binding.etEmail.text.toString()) }
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionToRegisterFragment())
        }
        binding.cwAuthByPhoneContainer.setOnClickListener {
            if (viewModel.authType != AuthType.PHONE) {
                viewModel.authType = AuthType.PHONE
                binding.cwAuthByPhoneContainer.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                binding.cwAuthByPhoneContainer.elevation = resources.getDimension(R.dimen.elevation_3)
                binding.llAutByPhoneContainer.visibility = View.VISIBLE
                binding.cwAuthByEmailContainer.setCardBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
                binding.cwAuthByEmailContainer.elevation = 0f
                binding.llAutByEmailContainer.visibility = View.GONE
                when(binding.tilCode.visibility == View.VISIBLE) {
                    true -> binding.btnSignIn.text = "Войти"
                    false -> binding.btnSignIn.text = "Выслать код"
                }
            }
        }
        binding.cwAuthByEmailContainer.setOnClickListener {
            if (viewModel.authType != AuthType.EMAIL) {
                viewModel.authType = AuthType.EMAIL
                binding.cwAuthByEmailContainer.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                binding.cwAuthByEmailContainer.elevation = resources.getDimension(R.dimen.elevation_3)
                binding.llAutByEmailContainer.visibility = View.VISIBLE
                binding.cwAuthByPhoneContainer.setCardBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
                binding.cwAuthByPhoneContainer.elevation = 0f
                binding.llAutByPhoneContainer.visibility = View.GONE
                binding.btnSignIn.text = "Войти"
            }
        }
    }

    override fun update() {
        viewModel.validateAuthEmail(binding.etEmail.text.toString(), binding.etPassword.text.toString())
    }

    private fun setupAuthByPhone() {
        binding.etPhone.setPhoneValidator {
            if (viewModel.trackErrorsForAuthByPhone) {
                when(FieldValidationsSettings.PHONE_REGEX.matches(it.toString())) {
                    true -> binding.tilPhone.error = null
                    false -> binding.tilPhone.error = "Неверный формат телефона"
                }
            }
        }
        val curTime = Date().time
        val expiredTime = ((curTime - viewModel.fetchLastRequestCodeDate() - viewModel.fetchLastRequestCodeTimeOut()*1000)/1000).toInt()
        when(expiredTime >= 0) {
            true -> {
                binding.tvExpired.visibility = View.GONE
                binding.tilCode.visibility = View.GONE
            }
            false -> {
                startConteDownTimer(-expiredTime)
                binding.etPhone.setText(viewModel.fetchLastAuthPhone())
                binding.tilCode.requestFocus()
            }
        }
    }

    private fun startConteDownTimer(seconds: Int) {
        viewModel.startCountDownTimer(seconds)
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Loading -> onStateLoading()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Hide -> onStateHide()
                is ViewState.Success -> {
                    if (viewModel.isAlreadyLogin()) {
                        viewModel.clearData()
                        flowViewModel.refresh()
                        cartFlowViewModel.refreshIdle()
                        favoriteViewModel.refreshIdle()
                        profileViewModel.refreshIdle()
                        findNavController().popBackStack()
                    } else {
                        onStateSuccess()
                    }
                }
            }
        }

        viewModel.loginErrorLD.observe(viewLifecycleOwner) { loginErrorMessage ->
            binding.tilPassword.error = loginErrorMessage
        }

        viewModel.errorLD.observe(viewLifecycleOwner) { errorMessage ->
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
        }

        viewModel.dialogMessageLD.observe(viewLifecycleOwner) { dialogMessage ->
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(dialogMessage)
                .setPositiveButton("Ок") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        viewModel.countDownTimerTickLD.observe(viewLifecycleOwner) {
            binding.tvExpired.setExpiredCodeText(it)
            binding.tvExpired.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_gray))
            binding.tvExpired.typeface = ResourcesCompat.getFont(requireContext(), R.font.rotonda_normal)
            binding.tvExpired.visibility = View.VISIBLE
            binding.btnSignIn.text = "Войти"
            binding.tilCode.visibility = View.VISIBLE
        }

        viewModel.codeInvalidErrorLD.observe(viewLifecycleOwner) {
            binding.tilCode.error = it
        }

        viewModel.countDownTimerFinishedLD.observe(viewLifecycleOwner) {
            if (it) {
                binding.tvExpired.text = "Отправить код повторно"
                binding.tvExpired.setTextColor(ContextCompat.getColor(requireContext(), R.color.bluePrimary))
                binding.tvExpired.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.rotonda_normal), Typeface.BOLD)
                binding.tvExpired.setOnClickListener { requestCode() }
            }
        }

        viewModel.requestCodeCompletedLD.observe(viewLifecycleOwner) {
            if (it) { binding.tilCode.requestFocus() }
        }
    }

    private fun requestCode() {
        viewModel.trackErrorsForAuthByPhone = true
        when(FieldValidationsSettings.PHONE_REGEX.matches(binding.etPhone.text.toString())) {
            true -> viewModel.requestCode(binding.etPhone.text.toString())
            false -> binding.tilPhone.error = "Неверный формат телефона"
        }
    }

}*/
