package com.vodovoz.app.ui.fragment.send_comment_about_shop

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.BsSendCommentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SendCommentAboutShopFlowBottomDialog : BaseBottomSheetFragment() {

    override fun layout() = R.layout.bs_send_comment

    private val binding: BsSendCommentBinding by viewBinding {
        BsSendCommentBinding.bind(
            contentView
        )
    }

    private val viewModel: SendCommentAboutShopFlowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initActionBar()
        initDialog()
        initSendButton()
        observeViewModel()
    }

    private fun initActionBar() {
        binding.incHeader.tvTitle.text = getString(R.string.new_comment_title)
        binding.incHeader.imgClose.setOnClickListener { dialog?.dismiss() }
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
        lifecycleScope.launch {
            viewModel.observeUiState().collect { state ->
                if (state.loadingPage) {
                    showLoader()
                } else {
                    hideLoader()
                }

                val sendCommentState = state.data
                if (sendCommentState.sendComplete) {
                    dialog?.cancel()
                    Toast.makeText(requireContext(), "Отзыв успешно добавлен!", Toast.LENGTH_SHORT)
                        .show()
                } else if (sendCommentState.error.isNotEmpty()) {
                    Toast.makeText(requireContext(), sendCommentState.error, Toast.LENGTH_SHORT)
                        .show()
                }

                showError(state.error)

            }
        }
    }
}