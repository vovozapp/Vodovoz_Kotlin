package com.vodovoz.app.ui.fragment.send_comment_about_product

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.databinding.BottomFragmentSendCommentBinding
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseBottomFragment
import com.vodovoz.app.ui.base.VodovozApplication

class SendCommentAboutProductBottomDialog : ViewStateBaseBottomFragment() {

    private lateinit var binding: BottomFragmentSendCommentBinding
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
    ) = BottomFragmentSendCommentBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        onStateSuccess()
        initActionBar()
        initDialog()
        initSendButton()
        observeViewModel()
    }

    override fun update() {}

    private fun initActionBar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.close.setOnClickListener { dialog?.dismiss() }
    }

    private fun initDialog() {
        dialog?.let {
            val behavior = (dialog as BottomSheetDialog).behavior
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun initSendButton() {
        binding.send.setOnClickListener {
            viewModel.validate(
                comment = binding.comment.text.toString(),
                rating = binding.rating.rating.toInt()
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