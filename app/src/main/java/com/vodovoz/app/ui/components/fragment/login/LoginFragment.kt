package com.vodovoz.app.ui.components.fragment.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.databinding.FragmentLoginBinding
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.components.base.FetchStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication

class LoginFragment : FetchStateBaseFragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[LoginViewModel::class.java]
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentLoginBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
    }.root

    override fun initView() {
        if (viewModel.isAlreadyLogin()) findNavController().popBackStack()
        initButtons()
        initAppBar()
        observeViewModel()
        onStateSuccess()
    }


    private fun initAppBar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.let { noNullActionBar ->
            noNullActionBar.setDisplayHomeAsUpEnabled(true)
            noNullActionBar.setDisplayShowHomeEnabled(true)
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initButtons() {
        binding.login.setOnClickListener {
            viewModel.validate(binding.email.text.toString(), binding.password.text.toString())
        }
        binding.recover.setOnClickListener {  }
        binding.register.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }
    }

    override fun update() {
        viewModel.validate(binding.email.text.toString(), binding.password.text.toString())
    }

    private fun observeViewModel() {
        viewModel.fetchStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is FetchState.Loading -> onStateLoading()
                is FetchState.Error -> onStateError(state.errorMessage)
                is FetchState.Hide -> {}
                is FetchState.Success -> {
                    when(state.data!!) {
                        true -> findNavController().popBackStack()
                        false -> onStateSuccess()
                    }
                }
            }
        }

        viewModel.loginErrorLD.observe(viewLifecycleOwner) { loginErrorMessage ->
            binding.passwordContainer.error = loginErrorMessage
        }
    }

}