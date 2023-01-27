package com.vodovoz.app.ui.fragment.pre_order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.databinding.BsPreOrderBinding
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseBottomFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.extensions.ContextExtensions.showSimpleMessageDialog
import com.vodovoz.app.ui.extensions.TextViewExtensions.setPhoneValidator
import com.vodovoz.app.util.FieldValidationsSettings
import com.vodovoz.app.util.PhoneSingleFormatUtil.convertPhoneToBaseFormat
import com.vodovoz.app.util.PhoneSingleFormatUtil.convertPhoneToFullFormat


class PreOrderBS : ViewStateBaseBottomFragment() {

    private lateinit var binding: BsPreOrderBinding
    private lateinit var viewModel: PreOrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        getArgs()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[PreOrderViewModel::class.java]
    }

    private fun getArgs() {
        viewModel.setupArgs(PreOrderBSArgs.fromBundle(requireArguments()).productId)
        viewModel.fetchPreOrderFormData()
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = BsPreOrderBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun update() { viewModel.fetchPreOrderFormData() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHeader()
        setupButtons()
        setupFields()
        observeViewModel()
        initDialog()
    }

    private fun initDialog() {
        dialog?.let {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.root.post {
                behavior.maxHeight = binding.root.bottom
            }
        }
    }

    private fun setupHeader() {
        binding.incHeader.imgClose.setOnClickListener { dismiss() }
        binding.incHeader.tvTitle.text = "Предзаказ товара"
        binding.tvName.text = PreOrderBSArgs.fromBundle(requireArguments()).productName
        Glide.with(requireContext()).load(PreOrderBSArgs.fromBundle(requireArguments()).productPictureUrl).into(binding.imgProduct)
    }

    private fun setupButtons() {
        binding.btnSend.setOnClickListener {
            when (checkValid()) {
                true -> viewModel.preOrderProduct(
                    name = binding.etName.text.toString(),
                    email = binding.etEmail.text.toString(),
                    phone = binding.etPhone.text.toString()
                )
                false -> Snackbar.make(binding.root, "Заполните парвильно все поля!", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun validateEmail(): Boolean {
        if (!FieldValidationsSettings.EMAIL_REGEX.matches(binding.etEmail.text.toString())) {
            if (viewModel.trackValidationErrors) {
                binding.tilEmail.error = "Неправильный формат почты"
            }
            return false
        } else  binding.tilEmail.error = null
        return true
    }

    private fun validateName(): Boolean {
        if (binding.etName.text.toString().length < 2) {
            if (viewModel.trackValidationErrors) {
                binding.tilName.error = "Неверное ФИО"
            }
            return false
        } else  binding.tilName.error = null
        return true
    }

    private fun validatePhone(): Boolean {
        if (!FieldValidationsSettings.PHONE_REGEX.matches(binding.etPhone.text.toString())) {
            if (viewModel.trackValidationErrors) {
                binding.tilPhone.error = "Неверный формат телефона"
            }
            return false
        } else binding.tilPhone.error = null
        return true
    }

    private fun checkValid(): Boolean {
        var isValid = true
        viewModel.trackValidationErrors = true

        if (!validateEmail()) isValid = false
        if (!validateName()) isValid = false
        if (!validatePhone()) isValid = false

        return isValid
    }

    private fun setupFields() {
        binding.etPhone.setPhoneValidator { validatePhone() }
        binding.etName.doAfterTextChanged { validateName() }
        binding.etEmail.doAfterTextChanged { validateEmail() }
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Loading -> onStateLoading()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Success -> onStateSuccess()
                is ViewState.Hide -> onStateHide()
            }
        }

        viewModel.successPreOrderMessageSLD.observe(viewLifecycleOwner) {
            requireContext().showSimpleMessageDialog(message = it) { dialog ->
                dialog.dismiss()
                findNavController().popBackStack()
            }
        }

        viewModel.errorLD.observe(viewLifecycleOwner) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
        }

        viewModel.preOrderFormDataUILD.observe(viewLifecycleOwner) {
            binding.etName.setText(it.name)
            binding.etPhone.setText(it.phone.convertPhoneToBaseFormat().convertPhoneToFullFormat())
            binding.etEmail.setText(it.email)
        }
    }

}