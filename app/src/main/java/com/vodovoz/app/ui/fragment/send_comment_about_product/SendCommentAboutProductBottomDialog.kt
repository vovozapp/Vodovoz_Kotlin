package com.vodovoz.app.ui.fragment.send_comment_about_product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsSendCommentBinding
import com.vodovoz.app.feature.productdetail.sendcomment.SendCommentAboutProductFlowViewModel
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseBottomFragment
import com.vodovoz.app.util.FieldValidationsSettings
import com.vodovoz.app.util.extensions.snack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SendCommentAboutProductBottomDialog : BaseBottomSheetFragment() {

    override fun layout(): Int = R.layout.bs_send_comment

    private val binding: BsSendCommentBinding by viewBinding { BsSendCommentBinding.bind(contentView) }

    private val viewModel: SendCommentAboutProductFlowViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initHeader()
        initSendButton()
        observeSendResult()
        initDialog()
    }

    private fun initDialog() {
        dialog?.let {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun observeSendResult() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeSendCommentResult()
                .collect {
                    if (it) {
                        requireActivity().snack("Отзыв успешно добавлен!")
                    } else {
                        requireActivity().snack("Ошибка, попробуйте снова.")
                    }
                }
        }
    }

    private fun initHeader() {
        binding.incHeader.imgClose.setOnClickListener { dialog?.dismiss() }
        binding.incHeader.tvTitle.text = getString(R.string.new_comment_title)
    }

    private fun initSendButton() {
        binding.btnSend.setOnClickListener {


            if(binding.rbRating.rating.toInt() == 0) {
                requireActivity().snack("Поставьте оценку от 1 до 5")
                return@setOnClickListener
            }

            if (binding.etComment.text.toString().length < FieldValidationsSettings.MIN_COMMENT_LENGTH) {
                requireActivity().snack("Длина отзыва должа быть не менее ${FieldValidationsSettings.MIN_COMMENT_LENGTH} символов")
                return@setOnClickListener
            }

            viewModel.sendCommentAboutProduct(
                comment = binding.etComment.text.toString(),
                rating = binding.rbRating.rating.toInt()
            )

        }
    }

}

/*
@AndroidEntryPoint
class SendCommentAboutProductBottomDialog : ViewStateBaseBottomFragment() {

    private lateinit var binding: BsSendCommentBinding
    private val viewModel: SendCommentAboutProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        getArgs()
    }

    private fun getArgs() {
        viewModel.updateArgs(SendCommentAboutProductBottomDialogArgs.fromBundle(requireArguments()).productId)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = BsSendCommentBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        onStateSuccess()
        initHeader()
        initDialog()
        initSendButton()
        observeViewModel()
    }

    override fun update() {}

    private fun initHeader() {
        binding.incHeader.imgClose.setOnClickListener { dialog?.dismiss() }
        binding.incHeader.tvTitle.text = getString(R.string.new_comment_title)
    }

    private fun initDialog() {
        dialog?.let {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun initSendButton() {
        binding.btnSend.setOnClickListener {
            viewModel.validate(
                comment = binding.etComment.text.toString(),
                rating = binding.rbRating.rating.toInt()
            )
        }
    }

    private fun observeViewModel() {
        viewModel.errorLD.observe(viewLifecycleOwner) { errorMessage ->
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
        }

        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Loading -> onStateLoading()
                is ViewState.Hide -> onStateHide()
                is ViewState.Error -> {
                    onStateSuccess()
                    Snackbar.make(binding.root, state.errorMessage, Snackbar.LENGTH_SHORT).show()
                }
                is ViewState.Success -> {
                    dialog?.cancel()
                    Snackbar.make(binding.root, "Отзыв успешно доавблен!", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

}*/
