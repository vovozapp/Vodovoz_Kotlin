package com.vodovoz.app.feature.preorder.old

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.databinding.BsPreOrderBinding
import com.vodovoz.app.feature.preorder.PreOrderBSArgs
import com.vodovoz.app.feature.preorder.PreOrderFlowViewModel
import com.vodovoz.app.ui.extensions.ContextExtensions.showSimpleMessageDialog
import com.vodovoz.app.ui.extensions.TextViewExtensions.setPhoneValidator
import com.vodovoz.app.util.FieldValidationsSettings
import com.vodovoz.app.util.PhoneSingleFormatUtil.convertPhoneToBaseFormat
import com.vodovoz.app.util.PhoneSingleFormatUtil.convertPhoneToFullFormat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PreOrderFlowFragment : BaseBottomSheetFragment() {

    override fun layout(): Int = R.layout.bs_pre_order

    private val binding: BsPreOrderBinding by viewBinding { BsPreOrderBinding.bind(contentView) }

    private val viewModel: PreOrderFlowViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    private val args: PreOrderBSArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.fetchPreOrderFormData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHeader()
        setupFields()
        initDialog()
        bindSendBtn()
        observeUiState()
        observePreOrderSuccess()
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect {
                    val detail = it.data.items ?: return@collect
                    binding.etName.setText(detail.name)
                    binding.etPhone.setText(detail.phone.convertPhoneToBaseFormat().convertPhoneToFullFormat())
                    binding.etEmail.setText(detail.email)
                }
        }
    }

    private fun observePreOrderSuccess() {
        lifecycleScope.launchWhenStarted {
            viewModel.observePreOrderSuccess()
                .collect {
                    requireContext().showSimpleMessageDialog(message = it) { dialog ->
                        dialog.dismiss()
                        findNavController().popBackStack()
                    }
                }
        }
    }


    private fun setupHeader() {
        binding.incHeader.imgClose.setOnClickListener {
            dismiss()
        }
        binding.incHeader.tvTitle.text = "Предзаказ товара"

        binding.tvName.text = args.productName
        Glide.with(requireContext())
            .load(args.productPictureUrl)
            .into(binding.imgProduct)
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

    private fun setupFields() {
        binding.etPhone.setPhoneValidator { validatePhone() }
        binding.etName.doAfterTextChanged { validateName() }
        binding.etEmail.doAfterTextChanged { validateEmail() }
    }

    private fun checkValid(): Boolean {
        var isValid = true

        if (!validateEmail()) isValid = false
        if (!validateName()) isValid = false
        if (!validatePhone()) isValid = false

        return isValid
    }

    private fun validateEmail(): Boolean {
        if (!FieldValidationsSettings.EMAIL_REGEX.matches(binding.etEmail.text.toString())) {
            binding.tilEmail.error = "Неправильный формат почты"
            return false
        } else  binding.tilEmail.error = null
        return true
    }

    private fun validateName(): Boolean {
        if (binding.etName.text.toString().length < 2) {
            binding.tilName.error = "Неверное ФИО"
            return false
        } else  binding.tilName.error = null
        return true
    }

    private fun validatePhone(): Boolean {
        if (!FieldValidationsSettings.PHONE_REGEX.matches(binding.etPhone.text.toString())) {
            binding.tilPhone.error = "Неверный формат телефона"
            return false
        } else binding.tilPhone.error = null
        return true
    }

    private fun bindSendBtn() {
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

}