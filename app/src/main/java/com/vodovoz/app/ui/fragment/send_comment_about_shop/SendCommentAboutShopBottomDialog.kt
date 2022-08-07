package com.vodovoz.app.ui.fragment.send_comment_about_shop

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.databinding.BsSendCommentBinding
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseBottomFragment
import com.vodovoz.app.ui.base.VodovozApplication

class SendCommentAboutShopBottomDialog : ViewStateBaseBottomFragment() {

    private lateinit var binding: BsSendCommentBinding
    private lateinit var viewModel: SendCommentAboutShopViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[SendCommentAboutShopViewModel::class.java]
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
        initActionBar()
        initDialog()
        initSendButton()
        observeViewModel()
    }

    override fun update() {}

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