package com.vodovoz.app.ui.fragment.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.databinding.FragmentLoginBinding
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderFragment

class LoginFragment : ViewStateBaseFragment() {

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
    ).apply { binding = this }.root

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
        binding.recoverPassword.setOnClickListener { viewModel.recoverPassword(binding.email.text.toString()) }
        binding.register.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionToRegisterFragment())
        }
    }

    override fun update() {
        viewModel.validate(binding.email.text.toString(), binding.password.text.toString())
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Loading -> onStateLoading()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Hide -> onStateHide()
                is ViewState.Success -> {
                    if (viewModel.isAlreadyLogin()) {
                        findNavController().popBackStack()
                    } else {
                        onStateSuccess()
                    }
                }
            }
        }

        viewModel.loginErrorLD.observe(viewLifecycleOwner) { loginErrorMessage ->
            binding.passwordContainer.error = loginErrorMessage
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
    }

}