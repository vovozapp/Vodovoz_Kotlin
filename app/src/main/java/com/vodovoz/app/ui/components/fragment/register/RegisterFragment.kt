package com.vodovoz.app.ui.components.fragment.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.databinding.FragmentRegisterBinding
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.components.base.FetchStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.fragment.login.LoginViewModel

class RegisterFragment : FetchStateBaseFragment() {

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
    ).apply {
        binding = this
    }.root

    override fun initView() {
        onStateSuccess()
        binding.register.setOnClickListener { viewModel.validate() }
        initFieldListeners()
        observeViewModel()
        initAppBar()
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

    private fun initFieldListeners() {
        with(binding) {
            firstName.addTextChangedListener { viewModel.firstName = it.toString() }
            secondName.addTextChangedListener { viewModel.secondName = it.toString() }
            email.addTextChangedListener { viewModel.email = it.toString() }
            phone.addTextChangedListener { viewModel.phone = it.toString() }
            password.addTextChangedListener { viewModel.password = it.toString() }
        }
    }

    override fun update() {
        viewModel.validate()
    }

    private fun observeViewModel() {
        viewModel.fetchStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is FetchState.Success -> when(state.data) {
                    null -> onStateSuccess()
                    else -> findNavController().popBackStack()
                }
                is FetchState.Error -> onStateError(state.errorMessage)
                is FetchState.Loading -> onStateLoading()
                is FetchState.Hide -> {}
            }
        }

        with(binding) {
            viewModel.firstNameErrorLD.observe(viewLifecycleOwner) { firstNameContainer.error = it }
            viewModel.secondNameErrorLD.observe(viewLifecycleOwner) { secondNameContainer.error = it }
            viewModel.phoneErrorLD.observe(viewLifecycleOwner) { phoneContainer.error = it }
            viewModel.emailErrorLD.observe(viewLifecycleOwner) { emailContainer.error = it }
            viewModel.passwordErrorLD.observe(viewLifecycleOwner) { passwordContainer.error = it }
        }
    }

}