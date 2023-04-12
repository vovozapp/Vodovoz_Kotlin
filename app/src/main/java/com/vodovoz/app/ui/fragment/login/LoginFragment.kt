package com.vodovoz.app.ui.fragment.login

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentLoginBinding
import com.vodovoz.app.feature.profile.ProfileFlowViewModel
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setExpiredCodeText
import com.vodovoz.app.ui.extensions.TextViewExtensions.setPhoneValidator
import com.vodovoz.app.ui.model.enum.AuthType
import com.vodovoz.app.util.FieldValidationsSettings
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class LoginFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private val profileViewModel: ProfileFlowViewModel by activityViewModels()

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

}