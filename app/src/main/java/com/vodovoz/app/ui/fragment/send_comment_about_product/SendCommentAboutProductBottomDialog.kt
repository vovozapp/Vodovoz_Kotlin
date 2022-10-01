package com.vodovoz.app.ui.fragment.send_comment_about_product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.databinding.BsSendCommentBinding
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseBottomFragment
import com.vodovoz.app.ui.base.VodovozApplication

class SendCommentAboutProductBottomDialog : ViewStateBaseBottomFragment() {

    private lateinit var binding: BsSendCommentBinding
    private lateinit var viewModel: SendCommentAboutProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        initViewModel()
        getArgs()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[SendCommentAboutProductViewModel::class.java]
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

}