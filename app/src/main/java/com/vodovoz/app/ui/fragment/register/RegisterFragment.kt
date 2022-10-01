package com.vodovoz.app.ui.fragment.register

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.databinding.FragmentRegisterBinding
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.extensions.FiledValidationsExtensions.setEmailValidation
import com.vodovoz.app.ui.extensions.FiledValidationsExtensions.setNameValidation
import com.vodovoz.app.ui.extensions.FiledValidationsExtensions.setPasswordValidation
import com.vodovoz.app.ui.extensions.FiledValidationsExtensions.setPhoneValidation
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class RegisterFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    private val compositeDisposable = CompositeDisposable()
    private val trackErrorSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val validEmailSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val validPhoneSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val validFirstNameSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val validSecondNameSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val validPasswordSubject: PublishSubject<Boolean> = PublishSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        subscribeSubjects()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[RegisterViewModel::class.java]
    }

    private fun subscribeSubjects() {
        validEmailSubject.subscribeBy { viewModel.validEmail = it }.addTo(compositeDisposable)
        validPhoneSubject.subscribeBy { viewModel.validPhone = it }.addTo(compositeDisposable)
        validFirstNameSubject.subscribeBy { viewModel.validFirstName = it }.addTo(compositeDisposable)
        validSecondNameSubject.subscribeBy { viewModel.validSecondName = it }.addTo(compositeDisposable)
        validPasswordSubject.subscribeBy { viewModel.validPassword = it }.addTo(compositeDisposable)
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
        setupFields()
        observeViewModel()
    }

    override fun update() {}

    private fun initAppBar() {
        binding.incAppBar.tvTitle.text = "Регистрация"
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initButtons() {
        binding.btnRegister.setOnClickListener {
            trackErrorSubject.onNext(true)
            Log.d(LogSettings.ID_LOG, "${viewModel.validEmail} ${viewModel.validPhone} ${viewModel.validFirstName} ${viewModel.validSecondName}")
            when(
                viewModel.validEmail &&
                        viewModel.validPhone &&
                        viewModel.validFirstName &&
                        viewModel.validSecondName &&
                        viewModel.validPassword
            ) {
                true -> viewModel.register(
                    firstName = binding.etFirstName.text.toString(),
                    secondName = binding.etSecondName.text.toString(),
                    email = binding.etEmail.text.toString(),
                    phone = binding.etPhone.text.toString(),
                    password = binding.etPassword.text.toString()
                )
                false -> {
                    Snackbar.make(binding.root, "Проверьте правильность введенных данных!", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupFields() {
        binding.etEmail.setEmailValidation(binding.tilEmail, compositeDisposable, trackErrorSubject, validEmailSubject)
        binding.etPhone.setPhoneValidation(binding.tilPhone, compositeDisposable, trackErrorSubject, validPhoneSubject)
        binding.etFirstName.setNameValidation(binding.tilFirstName, compositeDisposable, trackErrorSubject, validFirstNameSubject)
        binding.etSecondName.setNameValidation(binding.tilSecondName, compositeDisposable, trackErrorSubject, validSecondNameSubject)
        binding.etPassword.setPasswordValidation(binding.tilPassword, compositeDisposable, trackErrorSubject, validPasswordSubject, true)
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

        viewModel.errorLD.observe(viewLifecycleOwner) { message ->
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        }
    }

}