package com.vodovoz.app.ui.fragment.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.databinding.FragmentRegisterBinding
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication

class RegisterFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[RegisterViewModel::class.java]
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentRegisterBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        onStateSuccess()
        initAppBar()
        initButtons()
        initFieldListeners()
        observeViewModel()
    }

    private fun initAppBar() {
        binding.incAppBar.tvTitle.text = "Регистрация"
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initButtons() {
        binding.btnRegister.setOnClickListener { viewModel.validate() }
    }

    private fun initFieldListeners() {
        with(binding) {
            etFirstName.addTextChangedListener { viewModel.firstName = it.toString() }
            etSecondName.addTextChangedListener { viewModel.secondName = it.toString() }
            etEmail.addTextChangedListener { viewModel.email = it.toString() }
            etPhone.addTextChangedListener { viewModel.phone = it.toString() }
            etPassword.addTextChangedListener { viewModel.password = it.toString() }
        }
    }

    override fun update() {
        viewModel.validate()
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Success -> onStateSuccess()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Loading -> onStateLoading()
                is ViewState.Hide -> {}
            }
        }

        viewModel.isRegisterSuccessLD.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                findNavController().popBackStack()
            }
        }

        with(binding) {
            viewModel.firstNameErrorLD.observe(viewLifecycleOwner) { tilFirstName.error = it }
            viewModel.secondNameErrorLD.observe(viewLifecycleOwner) { tilSecondName.error = it }
            viewModel.phoneErrorLD.observe(viewLifecycleOwner) { tilPhone.error = it }
            viewModel.emailErrorLD.observe(viewLifecycleOwner) { tilEmail.error = it }
            viewModel.passwordErrorLD.observe(viewLifecycleOwner) { tilPassword.error = it }
        }
    }

}