package com.vodovoz.app.feature.productdetail.sendcomment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsSendCommentBinding
import com.vodovoz.app.util.FieldValidationsSettings
import com.vodovoz.app.util.extensions.snack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SendCommentAboutProductFlowFragment : BaseBottomSheetFragment() {

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